package org.purah.core.resolver;

import com.google.common.collect.Maps;
import org.purah.core.base.FieldGetMethodUtil;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.intf.FieldMatcherWithInstance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 反射解析器
 * 泛型为 Object 即支持所有类型
 * 会对字段配置进行
 */

public class ReflectArgResolver extends AbstractMatchArgResolver<Object> {

    ConcurrentHashMap<Class<?>, ClassConfigCache> classClassConfigCacheMap = new ConcurrentHashMap<>();
    FieldGetMethodUtil fieldGetMethodUtil;
    private static final String defaultBooleanPre = "is";
    private static final String defaultGetPre = "get";

    public ReflectArgResolver() {
        this(defaultGetPre, defaultBooleanPre);
    }

    public ReflectArgResolver(String getPre, String booleanPre) {
        this.fieldGetMethodUtil = new FieldGetMethodUtil(getPre, booleanPre);
    }


    @Override
    public Map<String, CheckInstance> getFieldsObjectMap(Object instance, Set<String> matchFieldList) {
        ClassConfigCache classConfigCache = initClassIfNecessary(instance.getClass());
        Map<String, CheckInstance> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchField : matchFieldList) {
            CheckInstance objectCheckInstance = classConfigCache.invoke(instance, matchField);
            result.put(matchField, objectCheckInstance);
        }
        return result;
    }


    @Override
    protected Set<String> fields(Object o) {
        return initClassIfNecessary(o.getClass()).fieldSet;
    }

    @Override
    protected Set<String> matchFieldList(Object instance, FieldMatcher fieldMatcher) {
        Class<?> instanceClass = instance.getClass();
        ClassConfigCache classConfigCache = initClassIfNecessary(instanceClass);
        return classConfigCache.matchFieldList(instance, fieldMatcher);

    }

    @Override
    public boolean support(Class<?> clazz) {
        if (Iterable.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (Iterator.class.isAssignableFrom(clazz)) {
            return false;
        }
        return !(Map.class.isAssignableFrom(clazz));
    }

    private ClassConfigCache initClassIfNecessary(Class<?> instanceClass) {
        return classClassConfigCacheMap.computeIfAbsent(instanceClass, i -> new ClassConfigCache(instanceClass));
    }


    protected class ClassConfigCache {
        Class<?> instanceClass;
        Set<String> fieldSet;
        Map<String, List<Annotation>> fieldAnnMap;
        Map<String, Field> fieldMap;
        Map<String, Method> fieldGetMethodMap;
        Map<FieldMatcher, Set<String>> matchFieldCacheMap = new ConcurrentHashMap<>();


        private CheckInstance invoke(Object instance, String fieldStr) {
            try {
                Method method = fieldGetMethodMap.get(fieldStr);
                if (method == null) {
                    throw new ArgResolverException("反射解析器" + this.getClass() + "找不到" + instance.getClass() + "字段" + fieldStr + "的get方法");
                }
                Field field = fieldMap.get(fieldStr);
                List<Annotation> annotations = fieldAnnMap.get(fieldStr);


                Object fieldObject = method.invoke(instance);
                return CheckInstance.createWithFieldConfig(fieldObject, fieldStr, field, annotations);
            } catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
                throw new ArgResolverException("反射解析器:" + this.getClass() + "在使用时出现异常 :" + instance.getClass() + "使用get方法出现异常" + e.getMessage());
            }
        }

        private ClassConfigCache(Class<?> instanceClass) {
            this.instanceClass = instanceClass;
            this.init(instanceClass);
        }

        public void init(Class<?> instanceClass) {


            fieldGetMethodMap = fieldGetMethodUtil.fieldNameGetMethodMap(instanceClass);
            fieldSet = fieldGetMethodMap.keySet();
            fieldMap = fieldGetMethodUtil.fieldNameFieldMap(instanceClass, fieldGetMethodMap.keySet());
            fieldAnnMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                List<Annotation> annList = Stream.of(entry.getValue().getDeclaredAnnotations()).collect(Collectors.toList());
                List<Annotation> cacheList = Collections.unmodifiableList(annList);
                fieldAnnMap.put(entry.getKey(), cacheList);
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
            if (fieldMatcher instanceof FieldMatcherWithInstance) {
                FieldMatcherWithInstance fieldMatcherWithInstance = (FieldMatcherWithInstance) fieldMatcher;
                result = fieldSet.stream().filter(field -> fieldMatcherWithInstance.match(field, instance)).collect(Collectors.toSet());
            } else {
                result = fieldMatcher.matchFields(fieldSet);
            }
            if (supportedCache) {
                matchFieldCacheMap.put(fieldMatcher, result);

            }
            return result;
        }


    }


}
