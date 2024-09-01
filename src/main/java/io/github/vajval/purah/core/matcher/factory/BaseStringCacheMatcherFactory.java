package io.github.vajval.purah.core.matcher.factory;


import io.github.vajval.purah.core.exception.FieldMatcherException;
import io.github.vajval.purah.core.exception.init.InitMatcherExceptionBase;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.name.NameUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 输入一个字符串,用FieldMatcher的单参string构造器生成 对象
 * 没有单参string构造器生成会报错,没有@Name 注解来获取名字也会报错
 * The most basic field matcher factory, which can create a factory by specifying the fieldMatcher class to use.
 * This factory generates a fieldMatcher of the specified class using a string parameter.
 */
public class BaseStringCacheMatcherFactory implements MatcherFactory {

    protected String name;
    protected Class<? extends FieldMatcher> fieldMatcherClazz;
    protected Constructor<? extends FieldMatcher> constructor;
    protected Map<String, FieldMatcher> cacheMap = new ConcurrentHashMap<>();

    /*
     *
     * It is recommended to inherit from the BaseStringMatcher class.
     * For the input class:
     * It must have a non-private constructor that accepts only a String as a parameter.
     * The class must have a @Name annotation on it.
     */
    public BaseStringCacheMatcherFactory(Class<? extends FieldMatcher> fieldMatcherClazz) {
        this.constructor = singleStringConstructor(fieldMatcherClazz);
        if (this.constructor == null) {
            throw new FieldMatcherException(fieldMatcherClazz.getName() + " No suitable constructor available. This method only supports constructors with a single String parameter.");
        }
        this.name = NameUtil.nameByAnnOnClass(fieldMatcherClazz);
        this.fieldMatcherClazz = fieldMatcherClazz;
    }

    @Override
    public FieldMatcher create(String matchStr) {
        return this.cacheMap.computeIfAbsent(matchStr, this::doCreate);
    }

    protected FieldMatcher doCreate(String matchStr) {
        try {
            return this.constructor.newInstance(matchStr);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InitMatcherExceptionBase(this.fieldMatcherClazz + "   :    " + matchStr);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    private static Constructor<? extends FieldMatcher> singleStringConstructor(Class<? extends FieldMatcher> fieldMatcherClazz) {
        try {
            return fieldMatcherClazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean clazzVerify(Class<?> clazz) {
        Constructor<? extends FieldMatcher> constructor = singleStringConstructor((Class) clazz);
        return constructor != null;
    }


}
