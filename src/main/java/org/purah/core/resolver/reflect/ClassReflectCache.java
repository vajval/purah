package org.purah.core.resolver.reflect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.matcher.FieldMatcher;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    private final Map<FieldMatcher, Map<String, Function<Object, InputToCheckerArg<?>>>> fieldMatcherMap = new ConcurrentHashMap<>();


    public void regNew(FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        Map<String, Function<Object, InputToCheckerArg<?>>> cacheInvoke = new ConcurrentHashMap<>();
        for (Map.Entry<String, InputToCheckerArg<?>> entry : result.entrySet()) {
            InputToCheckerArg<?> childArg = entry.getValue();
            cacheInvoke.put(entry.getKey(), i -> InputToCheckerArg.createChildWithFieldConfig(i, childArg.fieldStr(), childArg.field(), childArg.annListOnField()));
        }
        fieldMatcherMap.put(fieldMatcher, cacheInvoke);
    }

    public boolean cached(FieldMatcher fieldMatcher) {
        return fieldMatcherMap.containsKey(fieldMatcher);
    }

    public Map<String, InputToCheckerArg<?>> byCache(FieldMatcher fieldMatcher, Object inputArg) {
        Map<String, Function<Object, InputToCheckerArg<?>>> cacheInvoke = fieldMatcherMap.get(fieldMatcher);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(cacheInvoke.size());
        Map<String, Object> objectMap = this.objectMap(inputArg, cacheInvoke.keySet());
        for (Map.Entry<String, Function<Object, InputToCheckerArg<?>>> entry : cacheInvoke.entrySet()) {
            result.put(entry.getKey(), entry.getValue().apply(objectMap.get(entry.getKey())));
        }
        return result;
    }

    public Map<String, Object> objectMap(Object inputArg, Set<String> fields) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(fields.size());
        for (String field : fields) {
            result.put(field, get(inputArg, field));
        }
        return result;
    }

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
                        String childFieldStr = childStr(arg.fieldStr(), fieldName, ".");
                        Object childArg = get(arg.argValue(), fieldName);
                        return arg.createChildWithFieldConfig(childArg, childFieldStr, field, annotationList);
                    });
        }
    }

    protected Set<String> matchFieldList(Object instance, FieldMatcher fieldMatcher) {
        boolean supportedCache = fieldMatcher.supportCache();
        if (supportedCache) {
            return matchFieldCacheMap.computeIfAbsent(fieldMatcher, i -> fieldMatcher.matchFields(factoryCacheByClassField.keySet(), instance));
        }
        return fieldMatcher.matchFields(factoryCacheByClassField.keySet(), instance);
    }

    protected Map<String, InputToCheckerArg<?>> matchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = this.matchFieldList(inputToCheckerArg.argValue(), fieldMatcher);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchFieldStr : matchFieldList) {
            Function<InputToCheckerArg<?>, InputToCheckerArg<?>> function = factoryCacheByClassField.get(matchFieldStr);
            if (function == null) {
                String childFieldStr = childStr(inputToCheckerArg.fieldStr(), matchFieldStr, ".");
                Field field = fieldMap.get(matchFieldStr);
                if (field != null) {
                    logger.warn("set null value because not getter function for class {}, field: {}", inputToCheckerArg.argClass(), matchFieldStr);
                }
                InputToCheckerArg<?> objectInputToCheckerArg = inputToCheckerArg.createChildWithFieldConfig(null, childFieldStr, field, annMap.get(matchFieldStr));
                result.put(matchFieldStr, objectInputToCheckerArg);
                continue;
            }

            InputToCheckerArg<?> objectInputToCheckerArg = function.apply(inputToCheckerArg);
            result.put(matchFieldStr, objectInputToCheckerArg);
        }
        return result;
    }

    protected Object get(Object inputArg, String field) {
        if (inputArg == null) {
            return null;
        }
        try {
            return PropertyUtils.getProperty(inputArg, field);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
//            throw new UnexpectedException(e.getMessage());
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


    public static ClassReflectCache nullOrEmptyValueReflectCache = new ClassReflectCache() {

        @Override
        protected Map<String, InputToCheckerArg<?>> matchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
            Set<String> matchFieldList = fieldMatcher.matchFields(Collections.emptySet(), inputToCheckerArg.argValue());
            Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
            for (String matchField : matchFieldList) {
                String childStr = childStr(inputToCheckerArg.fieldStr(), matchField, ".");
                result.put(matchField, inputToCheckerArg.createChildWithFieldConfig(null, childStr, null, null));
            }
            return result;
        }
    };


}