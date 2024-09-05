package io.github.vajval.purah.core.resolver;

import com.google.common.collect.Maps;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.checker.ITCArgNullType;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/*
 * 缓存FieldMatcher执行之后最终结果的数据,缓存之后下次获取不需要执行FieldMatcher中的逻辑,直接获取结果
 * 前提是FieldMatcher及class支持缓存
 * result {a.b.c=arg("a.b.c",value,[@Ann1("v1"),@Ann2("2v")],field_info)}
 * cache
 * Function = (inputArg)-> new arg("a.b.c",invokeGet(inputArg,"a.b.c"),[@Ann1("v1"),@Ann2("2v")],field_info)
 * invoke cache to build result
 */

public class FieldMatcherResultReflectInvokeCache {

    protected static final Logger logger = LogManager.getLogger(FieldMatcherResultReflectInvokeCache.class);

    protected final Class<?> inputArgClass;
    protected final FieldMatcher cachedFieldMatcher;

    protected final Map<String, Function<Object, InputToCheckerArg<?>>> inputArgToResultNullInovkeMap = new ConcurrentHashMap<>();
    protected final int resultSize;
    protected final ReflectTrieCache reflectTrieCache;

    public FieldMatcherResultReflectInvokeCache(Class<?> inputArgClass, FieldMatcher cachedFieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        this.inputArgClass = inputArgClass;
        this.cachedFieldMatcher = cachedFieldMatcher;
        this.resultSize = result.size();
        this.reflectTrieCache = new ReflectTrieCache("");

        for (Map.Entry<String, InputToCheckerArg<?>> entry : result.entrySet()) {
            InputToCheckerArg<?> childArg = entry.getValue();
            String fullFieldPath = entry.getKey();
            if (childArg.isNull()) {
                if (childArg.nullType() == ITCArgNullType.no_field_no_getter) {
                    inputArgToResultNullInovkeMap.put(fullFieldPath, i -> InputToCheckerArg.createNullChildWithFieldConfig(childArg.fieldPath(), childArg.field(), childArg.annListOnField(), ITCArgNullType.no_field_no_getter));
                    continue;
                }
                if (childArg.nullType() == ITCArgNullType.have_field_no_getter) {
                    inputArgToResultNullInovkeMap.put(fullFieldPath, i -> {
                        logger.warn("set null value because not getter function for {}, field: {}", inputArgClass, childArg.field().getName());
                        return InputToCheckerArg.createNullChildWithFieldConfig(childArg.fieldPath(), childArg.field(), childArg.annListOnField(), ITCArgNullType.have_field_no_getter);
                    });
                    continue;
                }
            }
            reflectTrieCache.insert(fullFieldPath, childArg, fullFieldPath);
        }
    }


    public Map<String, InputToCheckerArg<?>> invokeResultByCache(Object inputArg) {
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(resultSize);
        reflectTrieCache.invoke(inputArg, result);

        for (Map.Entry<String, Function<Object, InputToCheckerArg<?>>> entry : inputArgToResultNullInovkeMap.entrySet()) {
            String field = entry.getKey();
            Function<Object, InputToCheckerArg<?>> invoke = entry.getValue();
            result.put(field, invoke.apply(null));
        }


        return result;
    }

    /**
     * 前缀树优化
     */

    public static class ReflectTrieCache {
        final List<ReflectTrieCache> childList = new ArrayList<>();
        final String fieldName;
        String fullPath;

        String argPath;
        Field argField;

        List<Annotation> argAnnListOnField;
        Method method;

        public ReflectTrieCache(String fieldName) {
            this.fieldName = fieldName;
        }

        public void insert(String path, InputToCheckerArg<?> arg, String fullPath) {
            String firstPath = ReflectUtils.firstPath(path);
            ReflectTrieCache node = getChildByFieldName(firstPath);
            if (firstPath.equals(path)) {
                node.argPath = arg.fieldPath();
                node.argField = arg.field();
                node.argAnnListOnField = arg.annListOnField();
                node.fullPath = fullPath;
            } else {
                String childPath = path.substring(firstPath.length() + 1);
                node.insert(childPath, arg, fullPath);
            }

        }

        public ReflectTrieCache getChildByFieldName(String fieldName) {
            for (ReflectTrieCache reflectTrieCache : childList) {
                if (reflectTrieCache.fieldName.equals(fieldName)) {
                    return reflectTrieCache;
                }
            }
            ReflectTrieCache result = new ReflectTrieCache(fieldName);
            childList.add(result);
            return result;
        }

        public void invoke(Object arg, Map<String, InputToCheckerArg<?>> resultMap) {
            if (this.fullPath != null) {
                resultMap.put(fullPath, InputToCheckerArg.createChildWithFieldConfig(arg, argPath, argField, argAnnListOnField));
            }
            for (ReflectTrieCache reflectTrieCache : childList) {
                if (arg == null) {
                    reflectTrieCache.invoke(null, resultMap);
                    continue;
                }
                if (reflectTrieCache.method == null) {
                    reflectTrieCache.method = ReflectUtils.getFieldMethod(arg.getClass(), reflectTrieCache.fieldName);
                }
                Object fieldValue = ReflectUtils.getByMethod(arg, reflectTrieCache.method);
                reflectTrieCache.invoke(fieldValue, resultMap);
            }

        }
    }


}