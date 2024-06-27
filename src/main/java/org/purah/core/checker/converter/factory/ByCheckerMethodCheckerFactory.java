package org.purah.core.checker.converter.factory;

import org.purah.core.checker.Checker;
import org.purah.core.checker.ProxyChecker;
import org.purah.core.checker.factory.CheckerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ByCheckerMethodCheckerFactory extends AbstractByMethodCheckerFactory implements CheckerFactory {


    public ByCheckerMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);
        String errorMsg = errorMsgCheckerFactoryByCheckerMethod(bean, method);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }
    }


    public static String errorMsgCheckerFactoryByCheckerMethod(Object bean, Method method) {
        Class returnType = method.getReturnType();

        if (!Checker.class.isAssignableFrom(returnType)) {
            return "返回类型只能是 Checker";
        }
        Parameter[] parameters = method.getParameters();

        if(!parameters[0].getParameterizedType().equals(String.class)){
            return "第一个入参必须是 string 类型，将被填充为checker名字";
        }
        return null;
    }


    @Override
    public Checker createChecker(String needMatchCheckerName) {


        try {
            Checker result = (Checker) method.invoke(bean, needMatchCheckerName);
            String name = needMatchCheckerName;
            if (StringUtils.hasText(result.name())) {
                name = result.name();
            }
            return new ProxyChecker(result, name, method.toGenericString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
