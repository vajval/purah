package org.purah.core.resolver;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.inft.ListIndexMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelMatchInfo;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 反射解析器
 * 泛型为 Object 即支持所有类型
 * 会对字段配置进行
 */

public class ReflectArgResolver implements ArgResolver {


    protected final ConcurrentHashMap<Class<?>, ClassReflectCache> classClassConfigCacheMap = new ConcurrentHashMap<>();


    protected boolean enableCache = true;

    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        if (fieldMatcher == null) {
            throw new RuntimeException("不要传空的fieldMatcher");
        }
        ClassReflectCache classReflectCache = classConfigCacheOf(inputToCheckerArg);
        Object argValue = inputToCheckerArg.argValue();

        if (classReflectCache.cached(fieldMatcher)) {
            return classReflectCache.byCache(argValue, fieldMatcher);
        }

        if (!enableCache || !fieldMatcher.supportCache()) {
            Map<String, InputToCheckerArg<?>> result = new HashMap<>();
            putMatchFieldObjectMapToResult(inputToCheckerArg, fieldMatcher, result);
            return result;
        }


        Map<String, InputToCheckerArg<?>> result = new HashMap<>();
        putMatchFieldObjectMapToResult(inputToCheckerArg, fieldMatcher, result);

        classReflectCache.tryRegNewCache(inputToCheckerArg, fieldMatcher, result);


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
                result.put(field, resultArg);
            }
            List<FieldMatcher> childFieldMatcherList = multilevelMatchInfo.getChildFieldMatcherList();
            //不需要往底层看
            if (CollectionUtils.isEmpty(childFieldMatcherList)) {
                continue;
            }
            for (FieldMatcher childFieldMatcher : childFieldMatcherList) {
                Map<String, InputToCheckerArg<?>> matchFieldObjectMap = getMatchFieldObjectMap(childArg, childFieldMatcher);
                for (Map.Entry<String, InputToCheckerArg<?>> argEntry : matchFieldObjectMap.entrySet()) {
                    String fullFieldStr = ClassReflectCache.childStr(field, argEntry.getKey(), ".");
                    argEntry.getValue().setFullFieldName(fullFieldStr);
                    result.put(fullFieldStr, argEntry.getValue());
                }

            }
        }
    }


    protected Map<String, InputToCheckerArg<?>> getThisLevelMatcherObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Class<?> inputArgClass = inputToCheckerArg.argClass();
        if (Map.class.isAssignableFrom(inputArgClass)) {
            return getResultFromMap((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        if (List.class.isAssignableFrom(inputArgClass)) {
            return getResultFromList((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        return getResultByReflect((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
    }


    private Map<String, InputToCheckerArg<?>> getResultByReflect(InputToCheckerArg<Object> inputToCheckerArg, FieldMatcher fieldMatcher) {

        return classConfigCacheOf(inputToCheckerArg).matchFieldValueMap(inputToCheckerArg, fieldMatcher);


    }


    private Map<String, InputToCheckerArg<?>> getResultFromMap(InputToCheckerArg<? extends Map<String, Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Map<String, Object> objectMap = inputToCheckerArg.argValue();
        Set<String> matchFieldList = fieldMatcher.matchFields(objectMap.keySet(), inputToCheckerArg);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchField : matchFieldList) {
            String childStr = ClassReflectCache.childStr(inputToCheckerArg.fieldStr(), matchField, ".");
            InputToCheckerArg<Object> childArg = InputToCheckerArg.createChild(objectMap.get(matchField), childStr);
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
                InputToCheckerArg<Object> childArg = InputToCheckerArg.createChild(value, matchField);
                result.put(matchField, childArg);
            }
            return result;
        }
        return Collections.emptyMap();

    }


    private static final HashSet<Class<?>> unSupportChildMatchClassSet = Sets.newHashSet(String.class, boolean.class, Boolean.class, int.class, Integer.class, short.class, Short.class, long.class, Long.class, byte.class, Byte.class, String.class, char.class, Character.class);


    private ClassReflectCache classConfigCacheOf(InputToCheckerArg<?> inputToCheckerArg) {


        Class<?> inputArgClass = inputToCheckerArg.argClass();

        ClassReflectCache result = classClassConfigCacheMap.get(inputArgClass);
        if (inputToCheckerArg.isNull()) {
            return ClassReflectCache.nullOrEmptyValueReflectCache;
        }

        if (result != null) {
            return result;
        }

        if (unSupportChildMatchClassSet.contains(inputArgClass)) {
            return ClassReflectCache.nullOrEmptyValueReflectCache;
        }
        return classClassConfigCacheMap.computeIfAbsent(inputArgClass, i -> new ClassReflectCache(inputArgClass));

    }


}
