package org.purah.core.checker.method.toChecker;

import org.purah.core.base.Name;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;


public class CheckerByLogicMethod extends AbstractMethodToChecker {


    public CheckerByLogicMethod(Object methodsToCheckersBean, Method method) {
        super(methodsToCheckersBean, method);


    }

    @Override
    public String name() {
        return purahEnableMethod.name();
    }

    @Override
    public PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahEnableMethod(methodsToCheckersBean, method);
    }

    @Override
    public CheckResult doCheck(CheckInstance checkInstance) {
        Object[] args = new Object[1];
        args[0] = purahEnableMethod.checkInstanceToInputArg(checkInstance);


        return purahEnableMethod.invoke(args);
    }

    @Override
    protected String validReturnErrorMsg(Object methodsToCheckersBean, Method method) {

        Name name = method.getAnnotation(Name.class);
        if (name == null) {
            return "必须要给规则一个名字 请在对应method上增加 @Name注解" + method;
        }


        if (method.getParameters().length != 1) {
            return "入参只能有一个参数" + method;
        }


        Class<?> returnType = method.getReturnType();

        if (!(CheckResult.class.isAssignableFrom(returnType)) &&
                !(boolean.class.isAssignableFrom(returnType))) {
            return "返回值必须是 CheckResult  或者 boolean " + method;

        }
        return null;

    }



}

