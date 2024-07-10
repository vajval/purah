package org.purah.core.resolver.reflect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.inft.ListIndexMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelMatchInfo;
import org.purah.core.resolver.ArgResolver;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 反射解析器
 * 泛型为 Object 即支持所有类型
 * 会对字段配置进行
 */

public class ReflectArgResolver implements ArgResolver {


    protected final ConcurrentHashMap<Class<?>, ClassReflectCache> classClassConfigCacheMap = new ConcurrentHashMap<>();


    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        if (fieldMatcher == null) {
            throw new RuntimeException("不要传空的fieldMatcher");
        }
        if (!fieldMatcher.supportCache()) {
            return doGetMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
        }
        ClassReflectCache classReflectCache = classConfigCacheOf(inputToCheckerArg);
        if (classReflectCache.cached(fieldMatcher)) {
            return classReflectCache.byCache(fieldMatcher, inputToCheckerArg.argValue());
        }
        Map<String, InputToCheckerArg<?>> result = doGetMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
        classReflectCache.regNew(fieldMatcher, result);
        return result;


    }


    public Map<String, InputToCheckerArg<?>> doGetMatchFieldObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Map<String, InputToCheckerArg<?>> result = new HashMap<>();
        putMatchFieldObjectMapToResult(inputToCheckerArg, fieldMatcher, result);
        return result;

    }


    public void putMatchFieldObjectMapToResult(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        if (fieldMatcher instanceof MultilevelFieldMatcher) {
            MultilevelFieldMatcher multilevelFieldMatcher = (MultilevelFieldMatcher) fieldMatcher;
            this.putMultiLevelMapToResult(inputToCheckerArg, multilevelFieldMatcher, result);
        } else {
            Map<String, InputToCheckerArg<?>> thisLevelMatcherObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, fieldMatcher);
            thisLevelMatcherObjectMap.forEach((a, b) -> result.put(b.fieldStr(), b));
        }
    }


    /**
     * 获取多级 matcher  从 instance中获取多级对象，
     */
    protected void putMultiLevelMapToResult(InputToCheckerArg<?> inputToCheckerArg, MultilevelFieldMatcher multilevelFieldMatcher, Map<String, InputToCheckerArg<?>> result) {

        Map<String, InputToCheckerArg<?>> fieldsObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, multilevelFieldMatcher);
        for (Map.Entry<String, InputToCheckerArg<?>> entry : fieldsObjectMap.entrySet()) {
            String field = entry.getKey();
            InputToCheckerArg<?> childArg = entry.getValue();
            MultilevelMatchInfo multilevelMatchInfo = multilevelFieldMatcher.childFieldMatcher(inputToCheckerArg, field, childArg);
            if (multilevelMatchInfo.isAddToFinal()) {
                InputToCheckerArg<?> resultArg = multilevelMatchInfo.getAddToFinalArg();
                result.put(resultArg.fieldStr(), resultArg);
            }
            List<FieldMatcher> childFieldMatcherList = multilevelMatchInfo.getChildFieldMatcherList();
            //不需要往底层看
            if (CollectionUtils.isEmpty(childFieldMatcherList)) {
                continue;
            }

            for (FieldMatcher childFieldMatcher : childFieldMatcherList) {
                this.putMatchFieldObjectMapToResult(childArg, childFieldMatcher, result);
            }
        }
    }


    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap2(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        if (fieldMatcher == null) {
            throw new RuntimeException("不要传空的fieldMatcher");
        }
        if (!fieldMatcher.supportCache()) {
            return doGetMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
        }
        ClassReflectCache classReflectCache = classConfigCacheOf(inputToCheckerArg);
        if (classReflectCache.cached(fieldMatcher)) {
            return classReflectCache.byCache(fieldMatcher, inputToCheckerArg.argValue());
        }
        Map<String, InputToCheckerArg<?>> result = doGetMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
        classReflectCache.regNew(fieldMatcher, result);
        return result;

    }


    protected void test(InputToCheckerArg<?> inputToCheckerArg, MultilevelFieldMatcher multilevelFieldMatcher, Map<String, InputToCheckerArg<?>> result) {

        Map<String, InputToCheckerArg<?>> thisLevelMatcherObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, multilevelFieldMatcher);

        for (Map.Entry<String, InputToCheckerArg<?>> entry : thisLevelMatcherObjectMap.entrySet()) {
            String field = entry.getKey();
            InputToCheckerArg<?> childArg = entry.getValue();
            MultilevelMatchInfo multilevelMatchInfo = multilevelFieldMatcher.childFieldMatcher(inputToCheckerArg, field, childArg);
            String fieldStr = ClassReflectCache.childStr(inputToCheckerArg.fieldStr(), field, ".");
            if (multilevelMatchInfo.isAddToFinal()) {
                InputToCheckerArg<?> resultArg = multilevelMatchInfo.getAddToFinalArg();
                result.put(fieldStr, resultArg);
            }
            List<FieldMatcher> childFieldMatcherList = multilevelMatchInfo.getChildFieldMatcherList();
            //不需要往底层看
            if (CollectionUtils.isEmpty(childFieldMatcherList)) {
                continue;
            }

            for (FieldMatcher childFieldMatcher : childFieldMatcherList) {
                Map<String, InputToCheckerArg<?>> matchFieldObjectMap = this.getMatchFieldObjectMap(childArg, childFieldMatcher);
                for (Map.Entry<String, InputToCheckerArg<?>> input : matchFieldObjectMap.entrySet()) {
                    String childStr = ClassReflectCache.childStr(fieldStr, input.getKey(), ".");
                    result.put(childStr, input.getValue());
                }
            }
        }
    }


    public Map<String, InputToCheckerArg<?>> getThisLevelMatcherObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {

        Class<?> inputArgClass = inputToCheckerArg.argClass();
        if (Map.class.isAssignableFrom(inputArgClass)) {
            return getResultFromMap((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        if (List.class.isAssignableFrom(inputArgClass)) {
            return getResultFromList((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        return getResultByReflect((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
    }


    @Override
    public Map<String, Object> objectMap(Object inputArg, Set<String> fields) {
        return null;
    }

    private Map<String, InputToCheckerArg<?>> getResultByReflect(
            InputToCheckerArg<Object> inputToCheckerArg, FieldMatcher fieldMatcher) {

        return classConfigCacheOf(inputToCheckerArg).matchFieldValueMap(inputToCheckerArg, fieldMatcher);


    }


    private Map<String, InputToCheckerArg<?>> getResultFromMap(InputToCheckerArg<? extends Map<String, Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Map<String, Object> objectMap = inputToCheckerArg.argValue();
        Set<String> matchFieldList = fieldMatcher.matchFields(objectMap.keySet(), inputToCheckerArg);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchField : matchFieldList) {
            String childStr = ClassReflectCache.childStr(inputToCheckerArg.fieldStr(), matchField, ".");
            InputToCheckerArg<Object> childArg = inputToCheckerArg.createChild(objectMap.get(matchField), childStr);
            result.put(matchField, childArg);
        }
        return result;
    }

    private Map<String, InputToCheckerArg<?>> getResultFromList(InputToCheckerArg<? extends List<Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {

        List<Object> objectList = inputToCheckerArg.argValue();
        if (fieldMatcher instanceof ListIndexMatcher) {
            ListIndexMatcher listIndexMatcher = (ListIndexMatcher) fieldMatcher;
            Map<String, Object> objectMap = listIndexMatcher.listMatch(objectList);

            Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(objectMap.size());
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                String matchField = entry.getKey();
                Object value = entry.getValue();
                String childStr = ClassReflectCache
                        .childStr(inputToCheckerArg.fieldStr(), matchField, "");
                InputToCheckerArg<Object> childArg = inputToCheckerArg.createChild(value, childStr);
                result.put(matchField, childArg);


            }
            return result;

        }
        return Collections.emptyMap();


    }


    private static HashSet<Class<?>> unSupportChildMatchClassSet = Sets.newHashSet(String.class,
            boolean.class, Boolean.class,
            int.class, Integer.class,
            short.class, Short.class,
            long.class, Long.class,
            byte.class, Byte.class, String.class, char.class, Character.class);


    private ClassReflectCache classConfigCacheOf(InputToCheckerArg<?> inputToCheckerArg) {
        Class<?> inputArgClass = inputToCheckerArg.argClass();
        if (unSupportChildMatchClassSet.contains(inputArgClass)) {
            return ClassReflectCache.nullOrEmptyValueReflectCache;
        }

        if (inputToCheckerArg.isNull()) {
            int modifiers = inputArgClass.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                return classClassConfigCacheMap.computeIfAbsent(inputArgClass, i -> new ClassReflectCache(inputArgClass));
            } else {
                return ClassReflectCache.nullOrEmptyValueReflectCache;
            }
        }
        return classClassConfigCacheMap.computeIfAbsent(inputArgClass, i -> new ClassReflectCache(inputArgClass));

    }


}
