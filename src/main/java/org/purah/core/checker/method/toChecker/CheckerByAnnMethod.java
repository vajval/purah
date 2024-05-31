package org.purah.core.checker.method.toChecker;

import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class CheckerByAnnMethod extends AbstractMethodToChecker {

    Class<?> annClazz;
    String name;

    public CheckerByAnnMethod(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method);
        this.name = name;
        annClazz = method.getParameters()[0].getType();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult doCheck(CheckInstance checkInstance) {
        Annotation annotation = checkInstance.annOf(annClazz);
        Object[] args = new Object[2];
        args[0] = annotation;
        args[1] = purahEnableMethod.checkInstanceToInputArg(checkInstance);
        return purahEnableMethod.invoke(args);


    }

    @Override
    public PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahEnableMethod(methodsToCheckersBean, method, 1);
    }


    @Override
    protected String validReturnErrorMsg(Object methodsToCheckersBean, Method method) {
//todo 第一个入参为注解
        if (method.getParameters().length != 2) {
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
