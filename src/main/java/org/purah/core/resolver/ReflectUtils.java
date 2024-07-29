package org.purah.core.resolver;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ResolvableType;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectUtils {
    protected static final Logger logger = LogManager.getLogger(ReflectUtils.class);
    private static PropertyDescriptor propertyDescriptor;

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

    public static boolean noExtendEnabledFields(Class<?> clazz, Set<String> fields) {
        Set<String> nestedFields = fields.stream().filter(i -> i.contains(".")).map(i -> i.substring(0, i.lastIndexOf("."))).collect(Collectors.toSet());
        return noExtendEnabledFields(clazz, nestedFields, Sets.newHashSet(clazz));
    }


    /**
     * 只有对象匹配到的所有的非叶子字段的class都是final
     * 检测所有 而且 final字段的所有字段 的class也都是final
     * 递归下去,只要有一个字段不是final就不行,因为 非final field的null值无法确定class
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


    private static boolean noExtendEnabledFields(Class<?> clazz, Set<String> fields, Set<Class<?>> clazzSet) {
        Map<String, PropertyDescriptor> collect = Stream.of(PropertyUtils.getPropertyDescriptors(clazz)).collect(Collectors.toMap(FeatureDescriptor::getName, i -> i));
        Map<String, Set<String>> fieldMap = fields.stream().collect(Collectors.groupingBy(field -> {
            if (field.contains(".")) {
                field = field.substring(0, field.indexOf("."));
            }
            if (field.contains("#")) {
                field = field.substring(0, field.indexOf("#"));
            }

            return field;
        }, Collectors.toSet()));
        for (Map.Entry<String, Set<String>> entry : fieldMap.entrySet()) {
            PropertyDescriptor propertyDescriptor = collect.get(entry.getKey());

            if (propertyDescriptor == null) {
                continue;
            }
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            if (Collection.class.isAssignableFrom(propertyType)) {
                ResolvableType[] generics = ResolvableType.forMethodReturnType(propertyDescriptor.getReadMethod()).as(Collection.class).getGenerics();
                if (generics.length > 0) {
                    propertyType = generics[0].resolve();
                } else {
                    propertyType = Object.class;
                }
            } else if (Map.class.isAssignableFrom(propertyType)) {
                ResolvableType[] generics = ResolvableType.forMethodReturnType(propertyDescriptor.getReadMethod()).as(Map.class).getGenerics();
                if (generics.length > 1) {
                    propertyType = generics[1].resolve();
                } else {
                    propertyType = Object.class;
                }
            }

            if (propertyType == null) {
                propertyType = Object.class;
            }
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
