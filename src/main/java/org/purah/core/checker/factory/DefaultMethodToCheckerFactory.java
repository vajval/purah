package org.purah.core.checker.factory;

import org.purah.core.checker.base.Checker;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.factory.bymethod.CheckerFactoryByLogicMethod;
import org.purah.core.checker.factory.bymethod.CheckerFactoryByMethod;
import org.purah.core.checker.factory.bymethod.MethodToCheckerFactory;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.springboot.ann.ToCheckerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class DefaultMethodToCheckerFactory implements MethodToCheckerFactory {
    @Override
    public CheckerFactory toCheckerFactory(Object bean, Method method) {

        ToCheckerFactory toCheckerFactory = method.getDeclaredAnnotation(ToCheckerFactory.class);

        String match = toCheckerFactory.match();
        int length = method.getParameters().length;
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();

        if (length == 2) {
            Parameter parameter1 = parameters[0];
            if (!parameter1.getType().equals(String.class)) {
                throw new RuntimeException("第一个入参必须是 string 类型，将被填充为checker名字");
            }
            boolean valid = PurahEnableMethod.validReturnType(returnType);
            if (!valid) {
                throw new RuntimeException("返回必须是 Boolean.class 或者 CheckResult.class");
            }


            return new CheckerFactoryByLogicMethod(bean, method, match);


        } else if (length == 1) {
            Parameter parameter = parameters[0];
            if (!parameter.getType().equals(String.class)) {
                throw new RuntimeException("唯一的入参必须是 string 类型，将被填充为checker名字");
            }
            if (!Checker.class.isAssignableFrom(returnType)) {
                throw new RuntimeException("返回值必须时checker");

            }

            return new CheckerFactoryByMethod(bean, method, match);

        } else {
            throw new RuntimeException();
        }


    }



}
