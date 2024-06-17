package org.purah.core.checker.factory.method;


import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.checker.Checker;
import org.purah.core.checker.PurahMethod;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ByLogicMethodCheckerFactory extends AbstractByMethodCheckerFactory implements CheckerFactory {


    PurahMethod purahEnableMethod;


    public ByLogicMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);
        String errorMsg = errorMsgCheckerFactoryByLogicMethod(bean, method);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }
        purahEnableMethod = new PurahMethod(bean, method, 1);

    }

    public static String errorMsgCheckerFactoryByLogicMethod(Object bean, Method method) {
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 2) {
            return "只支持两个入参，第一个为参数名字第二个为 需要检查的参数";
        }

        if (!parameters[0].getParameterizedType().equals(String.class)) {
            return "第一个入参必须是 string 类型，将被填充为checker名字";
        }
        if (CheckResult.class.isAssignableFrom(returnType)) {
            return null;
        }
        if (boolean.class.isAssignableFrom(returnType)) {
            return null;
        }
        return "返回值只能为 boolean或者CheckResult";
    }


    @Override
    public Checker createChecker(String needMatchCheckerName) {



        return new AbstractBaseSupportCacheChecker() {
            @Override
            public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {

                Object[] args = new Object[2];
                args[0] = needMatchCheckerName;
                args[1] = inputToCheckerArg;

                return purahEnableMethod.invokeResult(args);

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
