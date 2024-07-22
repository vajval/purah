package org.purah.core.checker.converter.factory;


import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.Checker;
import org.purah.core.checker.PurahWrapMethod;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.init.InitCheckFactoryException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ByLogicMethodCheckerFactory extends AbstractByMethodCheckerFactory implements CheckerFactory {


    PurahWrapMethod purahEnableMethod;

    public ByLogicMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);
        String errorMsg = errorMsgCheckerFactoryByLogicMethod(bean, method);
        if (errorMsg != null) {
            throw new InitCheckFactoryException(errorMsg);
        }
        purahEnableMethod = new PurahWrapMethod(bean, method, 1);

    }

    public static String errorMsgCheckerFactoryByLogicMethod(Object bean, Method method) {
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 2) {
            return "The function supports only two parameters: the first one is the parameter name, and the second one is the parameter to be checked.";
        }

        if (!parameters[0].getParameterizedType().equals(String.class)) {
            return "The first parameter must be of type string and will be filled with the name 'checker'.";
        }
        if (CheckResult.class.isAssignableFrom(returnType)) {
            return null;
        }
        if (boolean.class.isAssignableFrom(returnType)) {
            return null;
        }
        return "The return value can only be either a boolean or a `CheckResult`.";
    }


    @Override
    public Checker<?,?> createChecker(String needMatchCheckerName) {


        return new AbstractBaseSupportCacheChecker<Object,Object>() {
            @Override
            public CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {

                Object[] args = new Object[2];
                args[0] = needMatchCheckerName;
                args[1] = inputToCheckerArg;

                return purahEnableMethod.invokeResult(inputToCheckerArg, args);

            }

            @Override
            public String logicFrom() {
                return purahEnableMethod.logicFrom();
            }

            @Override
            public Class<?> inputArgClass() {
                return purahEnableMethod.needCheckArgClass();
            }

            @Override
            public Class<?> resultDataClass() {
                return purahEnableMethod.resultDataClass();
            }

            @Override
            public String name() {
                return needMatchCheckerName;
            }
        };
    }


}
