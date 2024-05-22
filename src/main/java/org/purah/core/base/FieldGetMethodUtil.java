package org.purah.core.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基础的字段函数解析器
 * 默认 布尔类型前缀为is
 * 其他的为get
 */
public class FieldGetMethodUtil {
    String booleanPre = "is";
    String getPre = "get";

    public FieldGetMethodUtil() {

    }

    public FieldGetMethodUtil(String getPre, String booleanPre) {
        this.getPre = getPre;
        this.booleanPre = booleanPre;

    }

    /**
     * 通过函数获取字段名
     * getUser（）-> user
     * isGood()-> good
     *
     * @param methodName
     * @return
     */

    public String methodNameToFieldName(String methodName) {
        String result;
        if (methodName.startsWith(booleanPre)) result = methodName.substring(booleanPre.length());
        else result = methodName.substring(getPre.length());
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    /**
     * class User{long id;String name}
     * 返回 {id:getId(),name:getName()}
     *
     * @param instanceClass
     * @return
     */

    public Map<String, Method> fieldNameGetMethodMap(Class<?> instanceClass) {
        return Stream.of(instanceClass.getDeclaredMethods())
                .filter(method -> usefulMethod(method.getName()))
                .filter(method -> method.getParameterCount() == 0)
                .collect(Collectors.toMap(method -> methodNameToFieldName(method.getName()), method -> method));
    }

    public Map<String, Field> fieldNameFieldMap(Class<?> instanceClass, Set<String> fieldStrSet) {
        return Stream.of(instanceClass.getDeclaredFields()).filter(i -> fieldStrSet.contains(i.getName()))
                .collect(Collectors.toMap(Field::getName, field -> field));


    }


    public Map<Field, Method> fieldGetMethodMap(Class<?> instanceClass) {
        Map<String, Method> methodMap = fieldNameGetMethodMap(instanceClass);
        Map<Field, Method> result = new HashMap<>();
        for (Map.Entry<String, Method> entry : methodMap.entrySet()) {
            String fieldName = entry.getKey();
            Method method = entry.getValue();
            try {
                Field field = instanceClass.getDeclaredField(fieldName);
                result.put(field, method);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

        }


        return result;
    }

    /**
     * 符合前缀要求的函数
     */

    public boolean usefulMethod(String methodName) {
        return methodName.startsWith(getPre) || methodName.startsWith(booleanPre);
    }
}
