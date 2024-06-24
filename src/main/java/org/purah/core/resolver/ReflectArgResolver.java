package org.purah.core.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.FieldMatcher;
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


    protected static String childStr(String parentFieldStr, String thisFieldStr, String levelSplitStr) {
        if (!StringUtils.hasText(parentFieldStr)) {
            return thisFieldStr;
        }
        return parentFieldStr + levelSplitStr + thisFieldStr;
    }

    @Override
    public Map<String, InputToCheckerArg<?>> getThisLevelMatcherObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {


        Object inputArg = inputToCheckerArg.argValue();
        if (inputArg == null) {
            throw new ArgResolverException("不支持 解析null:" + NameUtil.logClazzName(this));
        }
        Class<?> inputArgClass = inputArg.getClass();
        if (Map.class.isAssignableFrom(inputArgClass)) {
            return getResultFromMap((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        } else if (List.class.isAssignableFrom(inputArgClass)) {
            return getResultFromList((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
        }
        return getResultByReflect((InputToCheckerArg) inputToCheckerArg, fieldMatcher);
    }

    public Map<String, InputToCheckerArg<?>> getResultByReflect(
            InputToCheckerArg<Object> inputToCheckerArg, FieldMatcher fieldMatcher) {
        ClassConfigCache classConfigCache = classConfigCacheOf(inputToCheckerArg.argClass());
        return classConfigCache.matchFieldValueMap(inputToCheckerArg, fieldMatcher);
    }


    public Map<String, InputToCheckerArg<?>> getResultFromMap(InputToCheckerArg<? extends Map<String, Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Map<String, Object> objectMap = inputToCheckerArg.argValue();
        Set<String> matchFieldList = fieldMatcher.matchFields(objectMap.keySet());
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchField : matchFieldList) {
            String childStr = childStr(inputToCheckerArg.fieldStr(), matchField, ".");
            InputToCheckerArg<Object> childArg = inputToCheckerArg.createChild(objectMap.get(matchField), childStr);
            result.put(matchField, childArg);
        }
        return result;
    }

    public Map<String, InputToCheckerArg<?>> getResultFromList(InputToCheckerArg<? extends List<Object>> inputToCheckerArg, FieldMatcher fieldMatcher) {

        List<Object> objectList = inputToCheckerArg.argValue();
        Map<String, InputToCheckerArg<?>> result = new HashMap<>();
        for (int index = 0; index < objectList.size(); index++) {
            String fieldStr = "#" + index;
            boolean matched = fieldMatcher.match(fieldStr, objectList);

            if (matched) {
                String childStr = childStr(inputToCheckerArg.fieldStr(), fieldStr, "");
                InputToCheckerArg<Object> childArg = inputToCheckerArg.createChild(objectList.get(index), childStr);
                result.put(fieldStr, childArg);

            }
        }
        return result;

    }


    private ClassConfigCache classConfigCacheOf(Class<?> inputArgClass) {
        return classClassConfigCacheMap.computeIfAbsent(inputArgClass, i -> new ClassConfigCache(inputArgClass));
    }


    protected static class ClassConfigCache {
        Class<?> inputArgClass;

        Map<FieldMatcher, Set<String>> matchFieldCacheMap = new ConcurrentHashMap<>();

        Map<String, Function<InputToCheckerArg<?>, InputToCheckerArg<?>>> factoryCacheByClassField = new ConcurrentHashMap<>();


        private Set<String> fields() {
            return factoryCacheByClassField.keySet();
        }

        private ClassConfigCache(Class<?> inputArgClass) {
            this.inputArgClass = inputArgClass;
            this.init(inputArgClass);

        }

        protected Object get(Object inputArg, String field) {
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
                InputToCheckerArg<?> objectInputToCheckerArg = function.apply(inputToCheckerArg);
                result.put(matchFieldStr, objectInputToCheckerArg);

            }


            return result;
        }


        public void init(Class<?> inputArgClass) {
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

//            throw new ArgResolverException("反射解析器" + this.getClass() + "找不到" + instance.getClass() + "字段" + fieldStr + "的get方法");

//        throw new ArgResolverException("反射解析器:" + this.getClass() + "在使用时出现异常 :" + instance.getClass() + "使用get方法出现异常" + e.getMessage());

}
