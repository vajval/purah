package io.github.vajval.purah.core.resolver;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.vajval.purah.core.exception.UnexpectedException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ResolvableType;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    public static boolean noExtendEnabledFields(Class<?> clazz, Set<String> fields) {
        Set<String> nestedFields = fields.stream().filter(i -> i.contains(".")).map(i -> i.substring(0, i.lastIndexOf("."))).collect(Collectors.toSet());
        return noExtendEnabledFields(clazz, nestedFields, Sets.newHashSet(clazz));
    }


    /**
     * 只有对象匹配到的所有的非叶子字段的class都是final
     * 检测所有, 而且 final字段的所有字段 的class也都是final
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

    public static Object getByMethod(Object inputArg, Method method) {
        try {
            return method.invoke(inputArg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnexpectedException("getByMethod");
        }
    }

    public static Object get(Object inputArg, String field) {
        if (inputArg == null) {
            return null;
        }
        try {
            Method method = getMethodCacheMap.computeIfAbsent(inputArg.getClass(), ReflectUtils::getMethodMap).get(field);
            if (method == null) {
                return null;
            }
            return method.invoke(inputArg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("get {} value from {}", field, inputArg.getClass(), e);
            throw new UnexpectedException("invoke get");
        }
    }

    public static Method getFieldMethod(Class<?> clazz, String field) {
        return getMethodCacheMap.computeIfAbsent(clazz, i -> getMethodMap(clazz)).get(field);
    }


    static Map<Class<?>, Map<String, Method>> getMethodCacheMap = new ConcurrentHashMap<>();

    public static Map<String, Method> getMethodMap(Class<?> clazz) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        Map<String, Method> result = Maps.newHashMapWithExpectedSize(propertyDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            String name = propertyDescriptor.getName();
            result.put(name, readMethod);
        }
        return result;
    }

    public static Object getIgnoreNull(Object inputArg, String fullField) {
        Object result = inputArg;
        Iterable<String> split = Splitter.on(".").split(fullField);
        for (String field : split) {
            if (result == null) {
                return null;
            }
            result = get(result, field);
        }
        return result;
    }

}
