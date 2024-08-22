package io.github.vajval.purah.core.checker.converter.factory;

import io.github.vajval.purah.core.exception.UnexpectedException;
import io.github.vajval.purah.core.exception.init.InitCheckFactoryException;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.ProxyChecker;
import io.github.vajval.purah.core.checker.factory.CheckerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ByCheckerMethodCheckerFactory extends AbstractByMethodCheckerFactory implements CheckerFactory {


    public ByCheckerMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);
        String errorMsg = errorMsgCheckerFactoryByCheckerMethod(bean, method);
        if (errorMsg != null) {
            throw new InitCheckFactoryException(errorMsg);
        }
    }


    public static String errorMsgCheckerFactoryByCheckerMethod(Object bean, Method method) {
        Class<?> returnType = method.getReturnType();

        if (!Checker.class.isAssignableFrom(returnType)) {
            return "The return type can only be Checker, okay? That's how it is~";
        }
        Parameter[] parameters = method.getParameters();
        if (!parameters[0].getParameterizedType().equals(String.class)) {
            return "The first parameter must be of type string and will be filled with the checker's name.";
        }
        return null;
    }


    @Override
    public Checker<?,?> createChecker(String needMatchCheckerName) {
        try {
            Checker<?,?> result = (Checker) method.invoke(bean, needMatchCheckerName);
            String name = needMatchCheckerName;
            if (StringUtils.hasText(result.name())) {
                name = result.name();
            }
            return new ProxyChecker(result, name, method.toGenericString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnexpectedException(e.getMessage());
        }

    }
}
