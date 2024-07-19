package org.purah.core.resolver;

import com.google.common.base.Splitter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectUtils {
    protected static final Logger logger = LogManager.getLogger(ReflectUtils.class);

    public static String firstPath(String path) {
        if (path.contains(".")) {
            return path.substring(0, path.indexOf("."));
        }
        if (path.contains("#")) {
            return path.substring(0, path.indexOf("#"));
        }
        return path;

    }

    public static String childStr(String parentFieldStr, String thisFieldStr) {
        if (!StringUtils.hasText(parentFieldStr)) {
            return thisFieldStr;
        }
        if (thisFieldStr.startsWith("#")) {
            return parentFieldStr + thisFieldStr;
        }
        return parentFieldStr + "." + thisFieldStr;


    }


    /**
     * If this value is null, it's uncertain whether the object's class is People or SuperPeople,
     * potentially leading to errors in retrieving annotations.
     * If all fields in the People class are final, this issue doesn't need consideration.
     * class People{
     *
     * @Test(id) String id;
     * @Test(child) People child;   //
     * }
     * <p>
     *
     * <p>
     * class SuperPeople extend People{
     * @Test(superId) String id;   // ann change
     * }
     */


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

    protected static Object get(Object inputArg, String field) {
        if (inputArg == null) {
            return null;
        }
        try {
            return PropertyUtils.getProperty(inputArg, field);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("get {} value from {}", field, inputArg.getClass(), e);
            return null;
        }
    }

    public static Object getIgnoreNull(Object inputArg, String fullField) {
        if (inputArg == null) {
            return null;
        }
        Iterable<String> split = Splitter.on(".").split(fullField);
        Object result = inputArg;
        try {
            for (String field : split) {
                result = PropertyUtils.getProperty(result, field);
                if (result == null) {
                    return null;
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("get {} value from {}", fullField, inputArg.getClass(), e);
            return null;
        }

    }

}
