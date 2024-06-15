package org.purah.core.checker.factory.bymethod;


import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.factory.CheckerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class CheckerFactoryByLogicMethod extends BaseCheckerFactoryByMethod implements CheckerFactory {


    PurahEnableMethod purahEnableMethod;


    public CheckerFactoryByLogicMethod(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        super(bean, method, matchStr, cacheBeCreatedChecker);
        String errorMsg = errorMsgCheckerFactoryByLogicMethod(bean, method);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }
        purahEnableMethod = new PurahEnableMethod(bean, method, 1);

    }

    public static String errorMsgCheckerFactoryByLogicMethod(Object bean, Method method) {
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 2) {
            return "只支持两个入参，第一个为参数名字第二个为 需要检查的参数";
        }

        if(!parameters[0].getParameterizedType().equals(String.class)){
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


        return new BaseSupportCacheChecker() {
            @Override
            public CheckResult doCheck(InputCheckArg inputCheckArg) {

                Object[] args = new Object[2];
                args[0] = needMatchCheckerName;
                args[1] = purahEnableMethod.inputArgValue(inputCheckArg);
                Object result = purahEnableMethod.invoke(args);

                if (purahEnableMethod.resultIsCheckResultClass()) {
                    return (CheckResult) result;
                } else if (Objects.equals(result, true)) {
                    return success(inputCheckArg, true);
                } else if (Objects.equals(result, false)) {
                    return failed(inputCheckArg, false);
                }
                throw new RuntimeException("不阿盖出错");
            }

            @Override
            public String logicFrom() {
                return purahEnableMethod.wrapperMethod().toGenericString();
            }

            @Override
            public Class<?> inputCheckInstanceClass() {
                return purahEnableMethod.needCheckArgClass();
            }

            @Override
            public Class<?> resultClass() {
                return purahEnableMethod.resultWrapperClass();
            }

            @Override
            public String name() {
                return needMatchCheckerName;
            }
        };
    }


}
