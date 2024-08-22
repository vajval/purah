package io.github.vajval.purah.core.checker.converter.factory;

import io.github.vajval.purah.core.checker.combinatorial.CombinatorialChecker;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import io.github.vajval.purah.core.exception.init.InitCheckFactoryException;
import io.github.vajval.purah.core.checker.Checker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ByPropertiesMethodCheckerFactory extends AbstractByMethodCheckerFactory {


    public ByPropertiesMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);

        String errorMsg = errorMsgCheckerFactoryByPropertiesMethod(bean, method);
        if (errorMsg != null) {
            throw new InitCheckFactoryException(errorMsg);
        }
    }

    public static String errorMsgCheckerFactoryByPropertiesMethod(Object bean, Method method) {
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 1) {
            return "Only support 1 input parameter, which is the name to be matched.";
        }
        if (!parameters[0].getParameterizedType().equals(String.class)) {
            return "The first parameter must be of type String and represents the name to be matched.";
        }
        if (!CombinatorialCheckerConfig.class.isAssignableFrom(returnType)) {
            return "The return value must be of type `CombinatorialCheckerConfig`.";
        }
        return null;

    }

    @Override
    public Checker<?,?> createChecker(String needMatchCheckerName) {
        CombinatorialCheckerConfig config;
        try {
            config = (CombinatorialCheckerConfig) method.invoke(bean, needMatchCheckerName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return new CombinatorialChecker(config);
    }
}
