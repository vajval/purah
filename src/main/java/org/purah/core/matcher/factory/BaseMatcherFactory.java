package org.purah.core.matcher.factory;


import org.purah.core.name.NameUtil;
import org.purah.core.exception.FieldMatcherException;
import org.purah.core.exception.init.InitMatcherException;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * The most basic field matcher factory, which can create a factory by specifying the fieldMatcher class to use.
 * This factory generates a fieldMatcher of the specified class using a string parameter.
 */
public class BaseMatcherFactory implements MatcherFactory {

    protected String name;
    protected Class<? extends FieldMatcher> fieldMatcherClazz;
    protected Constructor<? extends FieldMatcher> constructor;

    public BaseMatcherFactory(Class<? extends FieldMatcher> fieldMatcherClazz) {
        initVerify(fieldMatcherClazz);
    }


    /**
     * It is recommended to inherit from the BaseStringMatcher class.
     * For the input class:
     * It must have a non-private constructor that accepts only a String as a parameter.
     * The class must have a @Name annotation on it.
     */

    public void initVerify(Class<? extends FieldMatcher> fieldMatcherClazz) {
        constructor = singleStringConstructor(fieldMatcherClazz);
        if (constructor == null) {
            throw new FieldMatcherException(fieldMatcherClazz.getName() + " No suitable constructor available. This method only supports constructors with a single String parameter.");
        }
        name = NameUtil.nameByAnnOnClass(fieldMatcherClazz);
        this.fieldMatcherClazz = fieldMatcherClazz;
    }

    public static Constructor<? extends FieldMatcher> singleStringConstructor(Class<? extends FieldMatcher> fieldMatcherClazz) {
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

    @Override
    public FieldMatcher create(String matchStr) {
        try {
            return constructor.newInstance(matchStr);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            //todo
            throw new InitMatcherException(fieldMatcherClazz + "   :    " + matchStr);

        }

    }

    @Override
    public String name() {
        return name;
    }
}
