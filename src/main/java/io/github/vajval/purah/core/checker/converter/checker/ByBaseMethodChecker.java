package io.github.vajval.purah.core.checker.converter.checker;

import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ByBaseMethodChecker extends AbstractWrapMethodToChecker {
    Checker<Object, Object> checker;

    public ByBaseMethodChecker(Object methodsToCheckersBean, Method method, String name,AutoNull autoNull) {
        super(methodsToCheckersBean, method, name,autoNull);

        String errorMsg = errorMsgCheckerByBaseMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        try {
            checker = (Checker ) method.invoke(methodsToCheckersBean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public CheckResult<Object> methodDoCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        return checker.check(inputToCheckerArg);
    }

    public static String errorMsgCheckerByBaseMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 0) {
            return "parameters length must be 0";
        }
        Class<?> returnType = method.getReturnType();
        if (!(Checker.class.isAssignableFrom(returnType))) {
            return "Only supports return types of Checker . [" + method + "]";

        }
        return null;


    }

    @Override
    public Class<?> inputArgClass() {
        return checker.inputArgClass();
    }

    @Override
    public Class<?> resultDataClass() {
        return checker.resultDataClass();
    }


    @Override
    public String logicFrom() {
        return checker.logicFrom();
    }

}
