package org.purah.core.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.checkerframework.checker.units.qual.C;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.ListIndexMatcher;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * 反射解析器
 * 泛型为 Object 即支持所有类型
 * 会对字段配置进行
 */

public class ReflectArgResolver extends AbstractMatchArgResolver {


    private final ConcurrentHashMap<Class<?>, ClassConfigCache> classClassConfigCacheMap = new ConcurrentHashMap<>();


    @Override
    public Map<String, InputToCheckerArg<?>> getThisLevelMatcherObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {


        if (inputToCheckerArg.isNull()) {
            return getResultByReflect((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        Class<?> inputArgClass = inputToCheckerArg.argClass();
        if (Map.class.isAssignableFrom(inputArgClass)) {
            return getResultFromMap((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        if (List.class.isAssignableFrom(inputArgClass)) {
            return getResultFromList((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        return getResultByReflect((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
    }

    public Map<String, InputToCheckerArg<?>> getResultByReflect(
            InputToCheckerArg<Object> inputToCheckerArg, FieldMatcher fieldMatcher) {
        if (inputToCheckerArg.isNull()) {
            return ClassConfigCache.emptyClassConfigCache.matchFieldValueMap(inputToCheckerArg, fieldMatcher);
        }


        ClassConfigCache classConfigCache = classConfigCacheOf(inputToCheckerArg);
        return classConfigCache.matchFieldValueMap(inputToCheckerArg, fieldMatcher);
    }


    public Map<String, InputToCheckerArg<?>> getResultFromMap(InputToCheckerArg<? extends Map<String, Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Map<String, Object> objectMap = inputToCheckerArg.argValue();
        Set<String> matchFieldList = fieldMatcher.matchFields(objectMap.keySet());
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchField : matchFieldList) {
            String childStr = ClassConfigCache.childStr(inputToCheckerArg.fieldStr(), matchField, ".");
            InputToCheckerArg<Object> childArg = inputToCheckerArg.createChild(objectMap.get(matchField), childStr);
            result.put(matchField, childArg);
        }
        return result;
    }

    public Map<String, InputToCheckerArg<?>> getResultFromList(InputToCheckerArg<? extends List<Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {

        List<Object> objectList = inputToCheckerArg.argValue();
        if (fieldMatcher instanceof ListIndexMatcher) {
            ListIndexMatcher listIndexMatcher = (ListIndexMatcher) fieldMatcher;
            Map<String, Object> objectMap = listIndexMatcher.listMatch(objectList);

            Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(objectMap.size());
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                String matchField = entry.getKey();
                Object value = entry.getValue();
                String childStr = ClassConfigCache
                        .childStr(inputToCheckerArg.fieldStr(), matchField, "");
                InputToCheckerArg<Object> childArg = inputToCheckerArg.createChild(value, childStr);
                result.put(matchField, childArg);


            }
            return result;

        }
        return Collections.emptyMap();


    }

    protected boolean supportChildGet(Class<?> clazz) {
        ArrayList<Class<?>> unSupportGet = Lists.newArrayList(String.class,
                boolean.class, Boolean.class,
                int.class, Integer.class,
                short.class, Short.class,
                long.class, Long.class,
                byte.class, Byte.class, String.class, char.class, Character.class);
        return !unSupportGet.contains(clazz);

    }

    private ClassConfigCache classConfigCacheOf(InputToCheckerArg<?> inputToCheckerArg) {

        if (inputToCheckerArg.isNull()) {
            return ClassConfigCache.emptyClassConfigCache;
        }
        Class<?> inputArgClass = inputToCheckerArg.argClass();
        if (!supportChildGet(inputArgClass)) {
            return ClassConfigCache.emptyClassConfigCache;
        }
        return classClassConfigCacheMap.computeIfAbsent(inputArgClass, i -> new ClassConfigCache(inputArgClass));
    }


//            throw new ArgResolverException("反射解析器" + this.getClass() + "找不到" + instance.getClass() + "字段" + fieldStr + "的get方法");

//        throw new ArgResolverException("反射解析器:" + this.getClass() + "在使用时出现异常 :" + instance.getClass() + "使用get方法出现异常" + e.getMessage());

}
