package org.purah.core.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.ITCArgNullType;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.matcher.FieldMatcher;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 对应类的反射信息缓存
 */
public class ClassReflectCache {

    protected static final Logger logger = LogManager.getLogger(ClassReflectCache.class);

    protected Class<?> inputArgClass;

    // fieldName的缓存
    protected Map<String, Field> fieldByNameCacheMap;
    protected Map<String, List<Annotation>> annByFieldNameCacheMap;
    protected final Map<String, Function<InputToCheckerArg<?>, InputToCheckerArg<?>>> fieldInvokeFunctionMapping = new ConcurrentHashMap<>();

    // FieldMatcher 匹配字段的缓存
    //todo 缓存区分
    protected final Map<FieldMatcher, Set<String>> matcherThisLevelFieldsCache = new ConcurrentHashMap<>();


    //FieldMatcher最终结果的缓存,缓存之后不需要执行FieldMatcher中的逻辑,直接获取结果
    protected final Set<FieldMatcher> noSupportInovekCacheFieldMatcherSet = new HashSet<>();
    protected final Map<FieldMatcher, FieldMatcherResultReflectInvokeCache> fieldMatcherResultByCacheInvokeMap = new ConcurrentHashMap<>();


    public ClassReflectCache() {
    }

    public ClassReflectCache(Class<?> inputArgClass) {
        if (inputArgClass == null) {
            throw new ArgResolverException("class not be null");
        }
        this.inputArgClass = inputArgClass;
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(inputArgClass);
        Field[] declaredFields = inputArgClass.getDeclaredFields();
        fieldByNameCacheMap = Stream.of(declaredFields).collect(Collectors.toMap(Field::getName, i -> i));
        annByFieldNameCacheMap = Stream.of(declaredFields).collect(Collectors.toMap(Field::getName, i -> Collections.unmodifiableList(Lists.newArrayList(i.getDeclaredAnnotations()))));
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            Field field = fieldByNameCacheMap.get(fieldName);
            List<Annotation> annotationList = annByFieldNameCacheMap.get(fieldName);
            fieldInvokeFunctionMapping.put(fieldName,
                    (arg) -> {
                        Object childArg = null;
                        if (arg.argValue() != null) {
                            childArg = ReflectUtils.get(arg.argValue(), fieldName);
                        }
                        return InputToCheckerArg.createChildWithFieldConfig(childArg, fieldName, field, annotationList);
                    });
        }
    }


    public static final ClassReflectCache nullOrEmptyValueReflectCache = new ClassReflectCache() {
        @Override
        public Map<String, InputToCheckerArg<?>> thisLevelMatchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
            Set<String> matchFieldList = fieldMatcher.matchFields(Collections.emptySet(), inputToCheckerArg.argValue());
            Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
            for (String matchField : matchFieldList) {
                result.put(matchField, InputToCheckerArg.createChildWithFieldConfig(null, matchField, null, null));
            }
            return result;
        }

        @Override
        public void tryRegNewInvokeCache(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        }
    };


    public Map<String, InputToCheckerArg<?>> thisLevelMatchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = matcherThisLevelFieldsCache.computeIfAbsent(fieldMatcher, i -> fieldMatcher.matchFields(fieldInvokeFunctionMapping.keySet(), inputToCheckerArg.argValue()));
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchFieldStr : matchFieldList) {
            Function<InputToCheckerArg<?>, InputToCheckerArg<?>> function = fieldInvokeFunctionMapping.get(matchFieldStr);
            if (function == null) {
                Field field = fieldByNameCacheMap.get(matchFieldStr);
                InputToCheckerArg<?> objectInputToCheckerArg;
                if (field != null) {
                    objectInputToCheckerArg = InputToCheckerArg.createNullChildWithFieldConfig(matchFieldStr, field, annByFieldNameCacheMap.get(matchFieldStr), ITCArgNullType.have_field_no_getter);
                    logger.warn("set null value because not getter function for class {}, field: {}", inputToCheckerArg.argClass(), matchFieldStr);
                } else {
                    objectInputToCheckerArg = InputToCheckerArg.createNullChildWithFieldConfig(matchFieldStr, null, null, ITCArgNullType.no_field_no_getter);
                }
                result.put(matchFieldStr, objectInputToCheckerArg);
                continue;
            }
            InputToCheckerArg<?> objectInputToCheckerArg = function.apply(inputToCheckerArg);
            result.put(matchFieldStr, objectInputToCheckerArg);
        }
        return result;
    }


    public Map<String, InputToCheckerArg<?>> fullResultByInvokeCache(Object inputArg, FieldMatcher fieldMatcher) {
        FieldMatcherResultReflectInvokeCache fieldMatcherResultReflectInvokeCache = fieldMatcherResultByCacheInvokeMap.get(fieldMatcher);
        if (fieldMatcherResultReflectInvokeCache == null) {
            throw new UnexpectedException(" no cache");
        }
        return fieldMatcherResultReflectInvokeCache.invokeResultByCache(inputArg);
    }


    public void tryRegNewInvokeCache(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        if (fieldMatcherResultByCacheInvokeMap.containsKey(fieldMatcher)) {
            return;
        }
        if (!fieldMatcher.supportCache()) {
            return;
        }
        if (noSupportInovekCacheFieldMatcherSet.contains(fieldMatcher)) {
            return;
        }


        boolean enabled = enableNestedGetValue(inputToCheckerArg, result);
        if (enabled) {
            fieldMatcherResultByCacheInvokeMap.computeIfAbsent(fieldMatcher, i -> new FieldMatcherResultReflectInvokeCache(inputArgClass, fieldMatcher, result));
        } else {
            noSupportInovekCacheFieldMatcherSet.add(fieldMatcher);
        }


    }


    public boolean cached(FieldMatcher fieldMatcher) {
        return fieldMatcherResultByCacheInvokeMap.containsKey(fieldMatcher);
    }


    protected static boolean enableNestedGetValue(InputToCheckerArg<?> inputToCheckerArg, Map<String, InputToCheckerArg<?>> result) {
        Object argValue = inputToCheckerArg.argValue();
        Class<?> inputArgClass = inputToCheckerArg.argClass();
        Set<String> nestedFields = result.keySet().stream().filter(i -> i.contains(".")).map(i -> i.substring(0, i.lastIndexOf("."))).collect(Collectors.toSet());

        if (!ReflectUtils.noExtendEnabledFields(inputArgClass, nestedFields, Sets.newHashSet(inputArgClass))) {
            return false;
        }
        //Directly retrieving values from nested fields yields the same results as the actual return values.
        for (Map.Entry<String, InputToCheckerArg<?>> entry : result.entrySet()) {
            InputToCheckerArg<?> childArg = entry.getValue();
            if (childArg.nullType() == ITCArgNullType.no_field_no_getter || childArg.nullType() == ITCArgNullType.have_field_no_getter) {
                continue;
            }
            Object ignoreNull = ReflectUtils.getIgnoreNull(argValue, entry.getKey());
            if (!Objects.equals(ignoreNull, entry.getValue().argValue())) {
                return false;
            }
        }
        return true;
    }


}