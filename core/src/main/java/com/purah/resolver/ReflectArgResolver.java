package com.purah.resolver;

import com.google.common.collect.Maps;
import com.purah.base.FieldGetMethodUtil;
import com.purah.exception.ArgResolverException;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.intf.FieldMatcherWithInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 反射解析器
 * 泛型为 Object 即支持所有类型
 * 会对字段配置进行
 */

public class ReflectArgResolver extends AbstractMatchArgResolver<Object> {

    Map<Class<?>, ClassConfigCache> classClassConfigCacheMap = new ConcurrentHashMap<>();
    FieldGetMethodUtil fieldGetMethodUtil;

    public ReflectArgResolver() {
        this.fieldGetMethodUtil = new FieldGetMethodUtil();
    }

    public ReflectArgResolver(String getPre, String booleanPre) {

        this.fieldGetMethodUtil = new FieldGetMethodUtil(getPre, booleanPre);
    }


    @Override
    public Map<String, Object> getFieldsObjectMap(Object instance, Set<String> matchFieldList) {

        Map<String, Method> methodMap = this.fieldMethodMap(instance.getClass());
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());
        for (String matchField : matchFieldList) {
            Method method = methodMap.get(matchField);
            result.put(matchField, invoke(instance, method));

        }
        return result;
    }


    /**
     * 反射解析器是这样的
     * 不应该传入一个不存在的get方法
     * 会报错而不是 返回null
     */

    public Object getObject(Object instance, String field) {
        Method method = this.fieldMethodMap(instance.getClass()).get(field);
        if (method == null) {
            throw new ArgResolverException("反射解析器" + this.getClass() + "找不到" + instance.getClass() + "字段" + field + "的get方法");
        }
        return this.invoke(instance, method);
    }

    private Object invoke(Object instance, Method method) {
        try {
            return method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new ArgResolverException("反射解析器:" + this.getClass() + "在使用时出现异常 :" + instance.getClass() + "使用get方法出现异常" + e.getMessage());
        }
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

    private Map<String, Method> fieldMethodMap(Class<?> instanceClass) {
        return initClassIfNecessary(instanceClass).fieldGetMethodMap;
    }

    protected class ClassConfigCache {
        Class<?> instanceClass;
        Set<String> fieldSet;
        Map<String, Method> fieldGetMethodMap;
        Map<FieldMatcher, Set<String>> matchFieldCacheMap = new ConcurrentHashMap<>();

        public ClassConfigCache(Class<?> instanceClass) {
            this.instanceClass = instanceClass;
            this.init(instanceClass);
        }

        public void init(Class<?> instanceClass) {


            fieldGetMethodMap = fieldGetMethodUtil.fieldNameGetMethodMap(instanceClass);
            fieldSet = fieldGetMethodMap.keySet();


        }

        protected Set<String> matchFieldList(Object instance, FieldMatcher fieldMatcher) {
            Set<String> result = matchFieldCacheMap.get(fieldMatcher);
            if (result != null) {
                return result;
            }
            if (fieldMatcher instanceof FieldMatcherWithInstance fieldMatcherWithInstance) {
                result = fieldSet.stream().filter(field -> fieldMatcherWithInstance.match(field, instance)).collect(Collectors.toSet());
            } else {
                result = fieldMatcher.matchFields(fieldSet);
            }
            matchFieldCacheMap.put(fieldMatcher, result);
            return result;
        }


    }


}
