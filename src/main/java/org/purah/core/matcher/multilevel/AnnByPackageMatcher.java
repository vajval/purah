package org.purah.core.matcher.multilevel;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.WildCardMatcher;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AnnByPackageMatcher implements MultilevelFieldMatcher {

    protected static final Map<Class<?>, Map<String, Supplier<MultilevelMatchInfo>>> clazzCacheMap = new ConcurrentHashMap<>();
    protected String allowPackagePatch;

    protected WildCardMatcher wildCardMatcher;

    public AnnByPackageMatcher(String matchStr) {
        this.allowPackagePatch = matchStr;
        wildCardMatcher = new WildCardMatcher(matchStr);
    }


    /**
     * 搜集有注解的字段做返回值
     * 还有 没有注解但是需要解析的向下解析
     */
    @Override
    public boolean match(String fieldStr, Object belongInstance) {
        Field field = field(fieldStr, belongInstance.getClass());
        if (field == null) {
            return false;
        }
        if (fieldCheck(field)) {
            return true;
        }
        return wildCardMatcher.match(field.getType().getPackage().getName());
    }

    @Override
    public MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        Class<?> clazz = inputArg.argClass();
        Map<String, Supplier<MultilevelMatchInfo>> cacheMap = clazzCacheMap.computeIfAbsent(clazz, i -> new ConcurrentHashMap<>());
        return cacheMap.computeIfAbsent(matchedField, i -> supplier(inputArg, matchedField, childArg)).get();

    }


    protected Supplier<MultilevelMatchInfo> supplier(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        Field field = field(matchedField, inputArg.argClass());
        if (field == null) {
            return MultilevelMatchInfo::ignore;
        }
        boolean match = wildCardMatcher.match(field.getType().getPackage().getName());
        if (match) {
            if (fieldCheck(field)) {
                return () -> MultilevelMatchInfo.addToFinalAndChildMatcher(this, childArg);
            } else {
                return () -> MultilevelMatchInfo.justChild(this);
            }
        } else {
            if (fieldCheck(field)) {
                return () -> MultilevelMatchInfo.addToFinal(childArg);
            } else {
                return MultilevelMatchInfo::ignore;
            }
        }
    }

    protected abstract boolean fieldCheck(Field field);

    protected static Map<String, Field> fieldMap(Class<?> instanceClazz) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(instanceClazz);
        HashMap<String, Field> result = Maps.newHashMapWithExpectedSize(propertyDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            Field declaredField;
            try {
                declaredField = instanceClazz.getDeclaredField(fieldName);
                result.put(fieldName, declaredField);
            } catch (NoSuchFieldException e) {
                continue;
            }

        }
        return result;
    }

    protected Field field(String fieldStr, Class<?> instanceClazz) {
        return fieldMap(instanceClazz).get(fieldStr);

    }


}
