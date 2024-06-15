package org.purah.core.checker.factory.bymethod;

import org.purah.core.PurahContext;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.factory.MethodToCheckerFactory;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CheckerFactoryByPropertiesMethod extends BaseCheckerFactoryByMethod {


    public CheckerFactoryByPropertiesMethod(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);

        String errorMsg = errorMsgCheckerFactoryByPropertiesMethod(bean, method);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }
    }

    public static String errorMsgCheckerFactoryByPropertiesMethod(Object bean, Method method) {
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 1) {
            return "只支持1个入参，参数为需要匹配的名字";
        }
        if (!parameters[0].getParameterizedType().equals(String.class)) {
            return "第一个参数只能String类型 是需要匹配的名字 ";
        }
        if (!CombinatorialCheckerConfig.class.isAssignableFrom(returnType)) {
            return "返回值只能为 CombinatorialCheckerConfig";
        }
        return null;

    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        CombinatorialCheckerConfig config;
        try {
            config = (CombinatorialCheckerConfig) method.invoke(bean, needMatchCheckerName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return new CombinatorialChecker(config);
    }
}
