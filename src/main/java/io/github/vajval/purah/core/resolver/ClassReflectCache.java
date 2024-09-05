package io.github.vajval.purah.core.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.vajval.purah.core.checker.ITCArgNullType;
import io.github.vajval.purah.core.exception.ArgResolverException;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

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


    //FieldMatcher最终结果的缓存,缓存之后不需要执行FieldMatcher中的逻辑,直接获取结果
    protected final Set<FieldMatcher> noSupportInovekCacheFieldMatcherSet = new HashSet<>();
    protected final Map<FieldMatcher, FieldMatcherResultReflectInvokeCache> fieldMatcherResultByCacheInvokeMap = new ConcurrentHashMap<>();
    protected final boolean enableExtendUnsafeCache;


    private ClassReflectCache() {
        this.enableExtendUnsafeCache = false;
    }


    public ClassReflectCache(Class<?> inputArgClass, boolean enableExtendUnsafeCache) {
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
        this.enableExtendUnsafeCache = enableExtendUnsafeCache;
    }

    /**
     * null值专用缓存
     */

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

        @Override
        public Map<String, InputToCheckerArg<?>> fullResultByInvokeCache(Object inputArg, FieldMatcher fieldMatcher) {
            return null;
        }
    };

    /*
     * 当前第一级字段的值,不支持嵌套
     * child.id -> child
     * id->id
     * child#1->child
     */


    public Map<String, InputToCheckerArg<?>> thisLevelMatchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = fieldMatcher.matchFields(fieldInvokeFunctionMapping.keySet(), inputToCheckerArg.argValue());
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchFieldStr : matchFieldList) {
            Function<InputToCheckerArg<?>, InputToCheckerArg<?>> function = fieldInvokeFunctionMapping.get(matchFieldStr);
            if (function == null) {
                Field field = fieldByNameCacheMap.get(matchFieldStr);
                InputToCheckerArg<?> objectInputToCheckerArg;
                if (field != null) {
                    objectInputToCheckerArg = InputToCheckerArg.createNullChildWithFieldConfig(matchFieldStr, field, annByFieldNameCacheMap.get(matchFieldStr), ITCArgNullType.have_field_no_getter);
                    logger.warn("set null value because not getter function for {}, field: {}", inputToCheckerArg.argClass(), matchFieldStr);
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

    /*
     * 直接通过反射缓存获取值
     * 不用 执行FieldMatcher中的逻辑
     */


    public Map<String, InputToCheckerArg<?>> fullResultByInvokeCache(Object inputArg, FieldMatcher fieldMatcher) {
        FieldMatcherResultReflectInvokeCache fieldMatcherResultReflectInvokeCache = fieldMatcherResultByCacheInvokeMap.get(fieldMatcher);
        if (fieldMatcherResultReflectInvokeCache == null) {
            return null;
        }
        return fieldMatcherResultReflectInvokeCache.invokeResultByCache(inputArg);
    }

    protected boolean supportCache(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        if (!fieldMatcher.supportCache()) {
            return false;
        }
        if (noSupportInovekCacheFieldMatcherSet.contains(fieldMatcher)) {
            return false;
        }
        boolean enabled = true;
        if (!enableExtendUnsafeCache) {
            enabled = ReflectUtils.noExtendEnabledFields(inputArgClass, result.keySet());
        }

        return enabled && enableNestedGetValue(inputToCheckerArg, result);
    }

    public void tryRegNewInvokeCache(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        if (fieldMatcherResultByCacheInvokeMap.containsKey(fieldMatcher)) {
            return;
        }
        boolean enabled = supportCache(inputToCheckerArg, fieldMatcher, result);
        if (enabled) {
            fieldMatcherResultByCacheInvokeMap.computeIfAbsent(fieldMatcher, i -> new FieldMatcherResultReflectInvokeCache(inputArgClass, fieldMatcher, result));
        } else {
            noSupportInovekCacheFieldMatcherSet.add(fieldMatcher);
        }


    }



    /*
     * 搜集返回结果的字段,直接从输入对象中生成结果
     * 生成的结果和返回的结果一样就是可以缓存的
     *
     */


    protected static boolean enableNestedGetValue(InputToCheckerArg<?> inputToCheckerArg, Map<String, InputToCheckerArg<?>> result) {
        Object argValue = inputToCheckerArg.argValue();
        for (String s : result.keySet()) {
            if (s.contains("#")) {
                return false;
            }
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