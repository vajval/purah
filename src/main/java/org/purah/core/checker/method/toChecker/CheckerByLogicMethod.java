package org.purah.core.checker.method.toChecker;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;
import java.util.Objects;


public class CheckerByLogicMethod extends AbstractMethodToChecker {


    public CheckerByLogicMethod(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        String errorMsg = errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

    }


    public CheckerByLogicMethod(Object methodsToCheckersBean, Method method) {
        this(methodsToCheckersBean, method, NameUtil.nameByAnnOnMethod(method));

    }

    @Override
    public PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahEnableMethod(methodsToCheckersBean, method);
    }

    @Override
    public CheckResult doCheck(InputCheckArg inputCheckArg) {
        Object[] args = new Object[1];
        args[0] = purahEnableMethod.inputArgValue(inputCheckArg);
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


    public static String errorMsgCheckerByLogicMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 1) {
            return "入参只能有一個將被填充為需要檢查的對象";
        }
        return null;


    }


}

