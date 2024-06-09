package org.purah.core.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.intf.FieldMatcher;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 反射解析器
 * 泛型为 Object 即支持所有类型
 * 会对字段配置进行
 */

public class ReflectArgResolver extends AbstractMatchArgResolver {

    private final ConcurrentHashMap<Class<?>, ClassConfigCache> classClassConfigCacheMap = new ConcurrentHashMap<>();


    @Override
    public Set<Class<?>> supportTypes() {
        return Sets.newHashSet(Object.class);
    }

    protected Set<String> fields(Object o) {
        return classConfigCacheOf(o.getClass()).fields();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return true;
    }

    @Override
    public Map<String, InputCheckArg<?>> getThisLevelMatcherObjectMap(Object instance, FieldMatcher fieldMatcher) {
        if (instance == null) {
            throw new ArgResolverException("不支持 解析null:" + NameUtil.logClazzName(this));
        }
        Class<?> instanceClass = instance.getClass();

        if (Map.class.isAssignableFrom(instanceClass)) {

            Map<String, Object> objectMap = (Map<String, Object>) instance;
            Set<String> matchFieldList = fieldMatcher.matchFields(objectMap.keySet());
            return matchFieldList.stream().collect(
                    Collectors.toMap(matchField -> matchField,
                            i -> InputCheckArg.create(objectMap.get(i), Object.class, i, instance)));
        }
        ClassConfigCache classConfigCache = classConfigCacheOf(instanceClass);
        return classConfigCache.matchFieldValueMap(instance, fieldMatcher);
    }


    private ClassConfigCache classConfigCacheOf(Class<?> instanceClass) {
        return classClassConfigCacheMap.computeIfAbsent(instanceClass, i -> new ClassConfigCache(instanceClass));
    }


    protected static class ClassConfigCache {
        Class<?> instanceClass;

        Map<FieldMatcher, Set<String>> matchFieldCacheMap = new ConcurrentHashMap<>();

        Map<String, Function<Object, InputCheckArg<?>>> factoryCacheByClassField = new ConcurrentHashMap<>();


        private Set<String> fields() {
            return factoryCacheByClassField.keySet();
        }

        private ClassConfigCache(Class<?> instanceClass) {
            this.instanceClass = instanceClass;
            this.init(instanceClass);
        }

        protected Object get(Object inputArg, String field) {
            try {
                return PropertyUtils.getProperty(inputArg, field);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        protected Map<String, InputCheckArg<?>> matchFieldValueMap(Object instance, FieldMatcher fieldMatcher) {
            Set<String> matchFieldList = this.matchFieldList(instance, fieldMatcher);
            Map<String, InputCheckArg<?>> result = Maps.newHashMapWithExpectedSize(matchFieldList.size());


            for (String matchFieldStr : matchFieldList) {
                Function<Object, InputCheckArg<?>> function = factoryCacheByClassField.get(matchFieldStr);
                InputCheckArg<?> objectInputCheckArg = function.apply(instance);
                result.put(matchFieldStr, objectInputCheckArg);

            }


            return result;
        }


        public void init(Class<?> instanceClass) {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(instanceClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String fieldName = propertyDescriptor.getName();
                Field declaredField;
                try {
                    declaredField = instanceClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                List<Annotation> annotations = Collections.unmodifiableList(Lists.newArrayList(declaredField.getDeclaredAnnotations()));
                factoryCacheByClassField.put(fieldName, parentObject -> InputCheckArg.createChildWithFieldConfig(get(parentObject, fieldName), declaredField, annotations, parentObject));
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
