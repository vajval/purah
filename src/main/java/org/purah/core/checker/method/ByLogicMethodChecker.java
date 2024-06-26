package org.purah.core.checker.method;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.PurahMethod;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;


public class ByLogicMethodChecker extends AbstractMethodToChecker {


    public ByLogicMethodChecker(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        String errorMsg = errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }
        purahEnableMethod = new PurahMethod(methodsToCheckersBean, method);


    }


    public ByLogicMethodChecker(Object methodsToCheckersBean, Method method) {
        this(methodsToCheckersBean, method, NameUtil.nameByAnnOnMethod(method));

    }


    @Override
    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {
        Object[] args = new Object[1];
        args[0] = inputToCheckerArg;
        return purahEnableMethod.invokeResult(inputToCheckerArg, args);
    }


    public static String errorMsgCheckerByLogicMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 1) {
            return "入参只能有一個將被填充為需要檢查的對象";
        }
        return null;


    }


}

