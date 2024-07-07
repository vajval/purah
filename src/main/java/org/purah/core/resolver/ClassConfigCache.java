package org.purah.core.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.inft.FieldMatcher;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ClassConfigCache {
    Class<?> inputArgClass;

    Map<String, Field> fieldMap = new ConcurrentHashMap<>();
    Map<String, List<Annotation>> annMap = new ConcurrentHashMap<>();

    Map<FieldMatcher, Set<String>> matchFieldCacheMap = new ConcurrentHashMap<>();

    Map<String, Function<InputToCheckerArg<?>, InputToCheckerArg<?>>> factoryCacheByClassField = new ConcurrentHashMap<>();

    protected static String childStr(String parentFieldStr, String thisFieldStr, String levelSplitStr) {
        if (!StringUtils.hasText(parentFieldStr)) {
            return thisFieldStr;
        }
        if (thisFieldStr.startsWith("#")) {
            return parentFieldStr + thisFieldStr;
        }
        return parentFieldStr + levelSplitStr + thisFieldStr;
    }

    public static ClassConfigCache emptyClassConfigCache = new ClassConfigCache(null) {

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

    private Set<String> fields() {
        return factoryCacheByClassField.keySet();
    }

    public ClassConfigCache(Class<?> inputArgClass) {
        this.inputArgClass = inputArgClass;
        this.init(inputArgClass);
    }

    protected Object get(Object inputArg, String field) {
        if (inputArg == null) {
            return null;
        }
        try {
            return PropertyUtils.getProperty(inputArg, field);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, InputToCheckerArg<?>> matchFieldValueMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = this.matchFieldList(inputToCheckerArg.argValue(), fieldMatcher);
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchFieldStr : matchFieldList) {
            Function<InputToCheckerArg<?>, InputToCheckerArg<?>> function = factoryCacheByClassField.get(matchFieldStr);

            if (function == null) {
                Field field = fieldMap.get(matchFieldStr);
                String childFieldStr = childStr(inputToCheckerArg.fieldStr(), matchFieldStr, ".");
                InputToCheckerArg<?> objectInputToCheckerArg = inputToCheckerArg.createChildWithFieldConfig(null, childFieldStr, field, annMap.get(matchFieldStr));
                result.put(matchFieldStr, objectInputToCheckerArg);
                continue;
            }
            InputToCheckerArg<?> objectInputToCheckerArg = function.apply(inputToCheckerArg);
            result.put(matchFieldStr, objectInputToCheckerArg);

        }


        return result;
    }


    public void init(Class<?> inputArgClass) {
        if (inputArgClass == null) {
            return;
        }
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(inputArgClass);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            Field declaredField;
            try {
                declaredField = inputArgClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            List<Annotation> annotations = Collections.unmodifiableList(Lists.newArrayList(declaredField.getDeclaredAnnotations()));


            fieldMap.put(fieldName, declaredField);
            annMap.put(fieldName, annotations);

            factoryCacheByClassField.put(fieldName,
                    (arg) -> {
                        String childFieldStr = childStr(arg.fieldStr(), fieldName, ".");
                        Object childArg = get(arg.argValue(), fieldName);
                        return arg.createChildWithFieldConfig(childArg, childFieldStr, declaredField, annotations);
                    });

        }
    }


    protected Set<String> matchFieldList(Object instance, FieldMatcher fieldMatcher) {
        boolean supportedCache = fieldMatcher.supportCache();
        Set<String> result = null;
        if (supportedCache) {
            result = matchFieldCacheMap.get(fieldMatcher);
        }
        if (result != null) {
            return result;
        }
        result = fieldMatcher.matchFields(factoryCacheByClassField.keySet(), instance);
        if (supportedCache) {
            matchFieldCacheMap.put(fieldMatcher, result);
        }
        return result;
    }


}