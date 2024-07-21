package org.purah.core.resolver;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.ITCArgNullType;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * result {a.b.c=arg("a.b.c",value,[@Ann1("v1"),@Ann2("2v")],field_info)}
 * cache  (inputArg)-> new arg("a.b.c",invokeGet(inputArg,"a.b.c"),[@Ann1("v1"),@Ann2("2v")],field_info)
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

    static class ReflectTrieCache {


        final Map<String, ReflectTrieCache> reflectNodeMap = new HashMap<>();


        final String fieldName;

        Field cacheField;
        List<Annotation> cacheAnnotationList;

        String cacheFieldStr;
        String fullPath;


        public ReflectTrieCache(String fieldName) {
            this.fieldName = fieldName;
        }

        public void insert(String path, InputToCheckerArg<?> arg, String fullPath) {
            String firstPath = ReflectUtils.firstPath(path);

            if (firstPath.equals(path)) {
                ReflectTrieCache node = reflectNodeMap.computeIfAbsent(path, i -> new ReflectTrieCache(path));
                node.fullPath = fullPath;
                node.cacheFieldStr = arg.fieldPath();
                node.cacheAnnotationList = arg.annListOnField();
                node.cacheField = arg.field();

            } else {
                String childPath = path.substring(firstPath.length() + 1);
                ReflectTrieCache reflectTrieCache = reflectNodeMap.computeIfAbsent(firstPath, i -> new ReflectTrieCache(firstPath));
                reflectTrieCache.insert(childPath, arg, fullPath);
            }

        }

        public void invoke(Object arg, Map<String, InputToCheckerArg<?>> resultMap) {


            if (this.fullPath != null) {
                resultMap.put(fullPath, InputToCheckerArg.createChildWithFieldConfig(arg, cacheFieldStr, cacheField, cacheAnnotationList));
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