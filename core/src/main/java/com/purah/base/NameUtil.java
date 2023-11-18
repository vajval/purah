package com.purah.base;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 获取注册或者打印日志时显示的名字
 */
public class NameUtil {

    public static final String NULL_OBJECT_SHOW_NAME = "<null对象>";

    private static Map<Class<?>, String> classAnnNameCacheMap = new ConcurrentHashMap<>();



    public static List<String> names(Object object, String... nameArray) {
        List<String> result = new ArrayList<>(nameArray.length + 3);
        result.addAll(NameUtil.allName(object));
        Collections.addAll(result,nameArray);
        return result;
    }

    /**
     * 通常是打印日志时显示一个对象名字用的
     * 优先级 Name接口 > Name注解 > 对象类名字
     */


    public static String useName(Object object) {
        if (object == null) return NULL_OBJECT_SHOW_NAME;
        String result = nameByInterface(object);
        if (result != null) return result;
        result = nameByClassNameAnn(object.getClass());
        if (result != null) return result;
        return object.getClass().getSimpleName();

    }




    private static String nameByInterface(Object object) {
        if (object == null) return null;
        if (object instanceof IName IName) {
            return IName.name();
        }
        return null;
    }

    public static String nameByClassNameAnn(Class<?> clazz) {
        if (clazz == null) return null;
        String result = classAnnNameCacheMap.get(clazz);
        if (result != null) return result;
       Name name = clazz.getDeclaredAnnotation(Name.class);
        if (name != null) {
            classAnnNameCacheMap.put(clazz, name.value());
            return name.value();
        }
        return null;
    }



    private static Set<String> allName(Object object) {
        if (object == null) {
            return Collections.singleton(NULL_OBJECT_SHOW_NAME);
        }
        Set<String> result = new HashSet<>();
        String nameByClassNameAnn = nameByClassNameAnn(object.getClass());
        String nameByInterface = nameByInterface(object);
        if (nameByClassNameAnn != null) {
            result.add(nameByClassNameAnn);
        }
        if (nameByInterface != null) {
            result.add(nameByInterface);
        }
        result.add(object.getClass().getName());
        return result;
    }


}
