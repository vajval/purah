package io.github.vajval.purah.core.resolver;

import com.google.common.collect.Maps;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.checker.ITCArgNullType;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

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

    protected static final Logger logger = LogManager.getLogger(ClassReflectCache.class);

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
                        logger.warn("set null value because not getter function for class {}, field: {}", inputArgClass, childArg.field().getName());
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

    static class ReflectTrieCache {


        final Map<String, ReflectTrieCache> reflectNodeMap = new HashMap<>();


        final String fieldName;

        String fullPath;

        Function<Object, InputToCheckerArg<?>> resultInvoke;


        public ReflectTrieCache(String fieldName) {
            this.fieldName = fieldName;
        }

        public void insert(String path, InputToCheckerArg<?> arg, String fullPath) {
            String firstPath = ReflectUtils.firstPath(path);

            if (firstPath.equals(path)) {
                ReflectTrieCache node = reflectNodeMap.computeIfAbsent(path, i -> new ReflectTrieCache(path));

                node.resultInvoke = object -> InputToCheckerArg.createChildWithFieldConfig(object, arg.fieldPath(), arg.field(), arg.annListOnField());

                node.fullPath = fullPath;

            } else {
                String childPath = path.substring(firstPath.length() + 1);
                ReflectTrieCache reflectTrieCache = reflectNodeMap.computeIfAbsent(firstPath, i -> new ReflectTrieCache(firstPath));
                reflectTrieCache.insert(childPath, arg, fullPath);
            }

        }

        public void invoke(Object arg, Map<String, InputToCheckerArg<?>> resultMap) {


            if (this.fullPath != null) {
                resultMap.put(fullPath, resultInvoke.apply(arg));
            }
            for (Map.Entry<String, ReflectTrieCache> entry : reflectNodeMap.entrySet()) {
                String key = entry.getKey();
                Object fieldValue = ReflectUtils.get(arg, key);
                ReflectTrieCache reflectTrieCache = entry.getValue();
                reflectTrieCache.invoke(fieldValue, resultMap);
            }
        }
    }


}