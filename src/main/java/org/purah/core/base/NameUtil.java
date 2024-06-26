package org.purah.core.base;


import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 获取注册或者打印日志时显示的名字
 */
public class NameUtil {

    public static final String NULL_OBJECT_SHOW_NAME = "<null_object>";
    private static final Map<Class<?>, String> classAnnNameCacheMap = new ConcurrentHashMap<>();
    private static final Map<Method, String> methodCacheMap = new ConcurrentHashMap<>();

    /**
     * 通常是打印日志时显示一个对象名字用的
     * 优先级 Name接口 > Name注解 > 对象类名字
     */


    public static String logClazzName(Object object) {
        if (object == null) return NULL_OBJECT_SHOW_NAME;
        if (object instanceof IName) {
            IName i = (IName) object;
            return i.name();
        }
        String result = nameByAnnOnClass(object.getClass());
        if (StringUtils.hasText(result)) return result;
        return object.getClass().getSimpleName();
    }


    public static String nameByAnnOnClass(Class<?> clazz) {
        if (clazz == null) return null;
        String result = classAnnNameCacheMap.get(clazz);
        if (StringUtils.hasText(result)) return result;
        Name name = clazz.getDeclaredAnnotation(Name.class);
        if (name !=null) {
            classAnnNameCacheMap.put(clazz, name.value());
            return name.value();
        }
        return null;
    }

    public static String nameByAnnOnMethod(Method method) {

        String result = methodCacheMap.get(method);
        if (StringUtils.hasText(result)) return result;
        Name name = method.getDeclaredAnnotation(Name.class);
        if (name != null) {
            methodCacheMap.put(method, name.value());
            return name.value();
        }
        return null;
    }


}
