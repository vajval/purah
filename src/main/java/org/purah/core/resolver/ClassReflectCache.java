package org.purah.core.resolver;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.ITCArgNullType;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.matcher.FieldMatcher;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassReflectCache {

    protected static final Logger logger = LogManager.getLogger(ClassReflectCache.class);

    private Class<?> inputArgClass;
    private Map<String, Field> fieldMap;
    private Map<String, List<Annotation>> annMap;
    private final Map<FieldMatcher, Set<String>> matchFieldCacheMap = new ConcurrentHashMap<>();
    private final Map<String, Function<InputToCheckerArg<?>, InputToCheckerArg<?>>> factoryCacheByClassField = new ConcurrentHashMap<>();
    public final Set<FieldMatcher> noSupportFieldMatcherSet = new HashSet<>();
    public final Map<FieldMatcher, Map<String, Function<Object, InputToCheckerArg<?>>>> fieldMatcherMap = new ConcurrentHashMap<>();
    public final Map<FieldMatcher, Map<String, Function<Object, InputToCheckerArg<?>>>> noGetterInvokeMap = new ConcurrentHashMap<>();


    public static ClassReflectCache nullOrEmptyValueReflectCache = new ClassReflectCache() {
        @Override
        public Map<String, InputToCheckerArg<?>> matchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
            Set<String> matchFieldList = fieldMatcher.matchFields(Collections.emptySet(), inputToCheckerArg.argValue());
            Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
            for (String matchField : matchFieldList) {
                result.put(matchField, InputToCheckerArg.createChildWithFieldConfig(null, matchField, null, null));
            }
            return result;
        }

        @Override
        public boolean tryRegNewCache(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
            return false;
        }
    };


    public ClassReflectCache() {
    }

    public ClassReflectCache(Class<?> inputArgClass) {
        if (inputArgClass == null) {
            throw new RuntimeException("class not be null");
        }
        this.inputArgClass = inputArgClass;
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(inputArgClass);
        Field[] declaredFields = inputArgClass.getDeclaredFields();
        fieldMap = Stream.of(declaredFields).collect(Collectors.toMap(Field::getName, i -> i));
        annMap = Stream.of(declaredFields).collect(Collectors.toMap(Field::getName, i -> Collections.unmodifiableList(Lists.newArrayList(i.getDeclaredAnnotations()))));
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            Field field = fieldMap.get(fieldName);
            List<Annotation> annotationList = annMap.get(fieldName);
            factoryCacheByClassField.put(fieldName,
                    (arg) -> {

                        Object childArg = null;
                        if (arg.argValue() != null) {
                            childArg = get(arg.argValue(), fieldName);
                        }
                        return InputToCheckerArg.createChildWithFieldConfig(childArg, fieldName, field, annotationList);
                    });
        }
    }

    protected static String childStr(String parentFieldStr, String thisFieldStr, String levelSplitStr) {
        if (!StringUtils.hasText(parentFieldStr)) {
            return thisFieldStr;
        }
        if (thisFieldStr.startsWith("#")) {
            return parentFieldStr + thisFieldStr;
        }
        return parentFieldStr + levelSplitStr + thisFieldStr;
    }


    protected Object get(Object inputArg, String field) {
        if (inputArg == null) {
            return null;
        }
        try {
            return PropertyUtils.getProperty(inputArg, field);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Set<String> matchFieldList(Object instance, FieldMatcher fieldMatcher) {
        return matchFieldCacheMap.computeIfAbsent(fieldMatcher, i -> fieldMatcher.matchFields(factoryCacheByClassField.keySet(), instance));
    }

    public Map<String, InputToCheckerArg<?>> matchFieldValueMap
            (InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = this.matchFieldList(inputToCheckerArg.argValue(), fieldMatcher);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchFieldStr : matchFieldList) {
            Function<InputToCheckerArg<?>, InputToCheckerArg<?>> function = factoryCacheByClassField.get(matchFieldStr);
            if (function == null) {
                Field field = fieldMap.get(matchFieldStr);
                InputToCheckerArg<?> objectInputToCheckerArg;
                if (field != null) {
                    objectInputToCheckerArg = InputToCheckerArg.createNullChildWithFieldConfig(matchFieldStr, field, annMap.get(matchFieldStr), ITCArgNullType.have_field_no_getter);
//                    logger.warn("set null value because not getter function for class {}, field: {}", inputToCheckerArg.argClass(), matchFieldStr);
                } else {
                    objectInputToCheckerArg = InputToCheckerArg.createNullChildWithFieldConfig(matchFieldStr, field, annMap.get(matchFieldStr), ITCArgNullType.no_field_no_getter);
                }
                result.put(matchFieldStr, objectInputToCheckerArg);
                continue;
            }

            InputToCheckerArg<?> objectInputToCheckerArg = function.apply(inputToCheckerArg);
            result.put(matchFieldStr, objectInputToCheckerArg);
        }
        return result;
    }

    protected Object getIgnoreNull(Object inputArg, String fullField) {
        Iterable<String> split = Splitter.on(".").split(fullField);
        Object result = inputArg;
        for (String field : split) {
            result = get(result, field);
            if (result == null) {
                return null;
            }
        }
        return result;
    }


    public Map<String, InputToCheckerArg<?>> byCache(Object inputArg, FieldMatcher fieldMatcher) {
        Map<String, Function<Object, InputToCheckerArg<?>>> cacheInvoke = fieldMatcherMap.get(fieldMatcher);
        if (cacheInvoke == null) {
            throw new UnexpectedException(" no cache");
        }
        Map<String, Function<Object, InputToCheckerArg<?>>> noGetterInvoke = noGetterInvokeMap.get(fieldMatcher);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(cacheInvoke.size() + noGetterInvoke.size());
        for (Map.Entry<String, Function<Object, InputToCheckerArg<?>>> entry : cacheInvoke.entrySet()) {
            String field = entry.getKey();
            Function<Object, InputToCheckerArg<?>> invoke = entry.getValue();
            Object ignoreNull;
            ignoreNull = getIgnoreNull(inputArg, field);
            result.put(field, invoke.apply(ignoreNull));
        }

        for (Map.Entry<String, Function<Object, InputToCheckerArg<?>>> entry : noGetterInvoke.entrySet()) {
            String field = entry.getKey();
            Function<Object, InputToCheckerArg<?>> invoke = entry.getValue();
            result.put(field, invoke.apply(null));
        }


        return result;

    }

    public static boolean noExtendEnabledFields(Class<?> clazz, Set<String> fields, Set<Class<?>> clazzSet) {

        Map<String, PropertyDescriptor> collect = Stream.of(PropertyUtils.getPropertyDescriptors(clazz)).collect(Collectors.toMap(FeatureDescriptor::getName, i -> i));

        Map<String, Set<String>> fieldMap = fields.stream().collect(Collectors.groupingBy(field -> {
            if (field.contains(".")) {
                return field.substring(0, field.indexOf("."));
            }
            return field;
        }, Collectors.toSet()));

        for (Map.Entry<String, Set<String>> entry : fieldMap.entrySet()) {
            PropertyDescriptor propertyDescriptor = collect.get(entry.getKey());
            if (propertyDescriptor == null) {
                continue;
            }
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            int modifiers = propertyType.getModifiers();
            if (!Modifier.isFinal(modifiers)) {
                return false;
            }
            if (clazzSet.contains(propertyType)) continue;
            clazzSet.add(propertyType);
            if (!noExtendEnabledFields(propertyType, fields, clazzSet)) {
                return false;
            }
        }


        return true;


    }


    public boolean tryRegNewCache(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {

        if (fieldMatcherMap.containsKey(fieldMatcher)) {
            return true;
        }
        if (!fieldMatcher.supportCache()) {
            return false;
        }
        if (noSupportFieldMatcherSet.contains(fieldMatcher)) {
            return false;
        }
        Object argValue = inputToCheckerArg.argValue();

        Set<String> fields = result.keySet().stream().filter(i -> i.contains(".")).map(i -> i.substring(0, i.lastIndexOf("."))).collect(Collectors.toSet());
        if (!noExtendEnabledFields(inputArgClass, fields, Sets.newHashSet(inputArgClass))) {
            noSupportFieldMatcherSet.add(fieldMatcher);
            return false;
        }


        for (Map.Entry<String, InputToCheckerArg<?>> entry : result.entrySet()) {
            InputToCheckerArg<?> childArg = entry.getValue();
            if (childArg.nullType() == ITCArgNullType.no_field_no_getter || childArg.nullType() == ITCArgNullType.have_field_no_getter) {
                continue;

            }
            Object ignoreNull = getIgnoreNull(argValue, entry.getKey());
            if (!Objects.equals(ignoreNull, entry.getValue().argValue())) {
                return false;
            }
        }


        Map<String, Function<Object, InputToCheckerArg<?>>> cacheInvoke = new ConcurrentHashMap<>();
        Map<String, Function<Object, InputToCheckerArg<?>>> noGetterCacheInvoke = new ConcurrentHashMap<>();
        for (Map.Entry<String, InputToCheckerArg<?>> entry : result.entrySet()) {

            InputToCheckerArg<?> childArg = entry.getValue();
            String fieldName = entry.getKey();
            if (childArg.isNull()) {
                if (childArg.nullType() == ITCArgNullType.no_field_no_getter) {
                    noGetterCacheInvoke.put(fieldName, i -> InputToCheckerArg.createNullChildWithFieldConfig(childArg.fieldStr(), childArg.field(), childArg.annListOnField(), ITCArgNullType.no_field_no_getter));
                    continue;
                }
                if (childArg.nullType() == ITCArgNullType.have_field_no_getter) {
//                    function = ;
                    noGetterCacheInvoke.put(fieldName, i -> {
//                        logger.warn("set null value because not getter function for class {}, field: {}", inputToCheckerArg.argClass(), childArg.field().getName());
                        return InputToCheckerArg.createNullChildWithFieldConfig(childArg.fieldStr(), childArg.field(), childArg.annListOnField(), ITCArgNullType.have_field_no_getter);
                    });
                    continue;
                }
            }
            cacheInvoke.put(fieldName, i -> InputToCheckerArg.createChildWithFieldConfig(i, childArg.fieldStr(), childArg.field(), childArg.annListOnField()));
        }
        fieldMatcherMap.put(fieldMatcher, cacheInvoke);
        noGetterInvokeMap.put(fieldMatcher, noGetterCacheInvoke);
        return true;
    }


    public boolean cached(FieldMatcher fieldMatcher) {
        return fieldMatcherMap.containsKey(fieldMatcher);
    }


}