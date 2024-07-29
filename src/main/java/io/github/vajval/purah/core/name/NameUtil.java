package io.github.vajval.purah.core.name;


import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 输入对象,获取名字
 * Input object, retrieve name.
 */
public class NameUtil {

    private static final String NULL_OBJECT_SHOW_NAME = "<null_object>";
    private static final Map<Class<?>, String> classAnnNameCacheMap = new ConcurrentHashMap<>();
    private static final Map<Method, String> methodCacheMap = new ConcurrentHashMap<>();

    /*
     * Priority: IName > @Name > className
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
