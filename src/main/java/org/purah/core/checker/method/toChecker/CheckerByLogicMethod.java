package org.purah.core.checker.method.toChecker;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;


public class CheckerByLogicMethod extends AbstractMethodToChecker {


    public CheckerByLogicMethod(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        if (name == null) {
            throw new RuntimeException("必须要给规则一个名字 请在对应method上增加 @Name注解" + method);
        }
        Class<?> returnType = method.getReturnType();

        if (!(CheckResult.class.isAssignableFrom(returnType)) &&
                !(boolean.class.isAssignableFrom(returnType))) {
            throw new RuntimeException("返回值必须是 CheckResult  或者 boolean " + method);

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
        args[0] = purahEnableMethod.checkInstanceToInputArg(inputCheckArg);


        return purahEnableMethod.invoke(args);
    }

    @Override
    protected String validReturnErrorMsg(Object methodsToCheckersBean, Method method) {


        if (method.getParameters().length != 1) {
            return "入参只能有一个参数" + method;
        }


        return null;

    }


}

