package org.purah.core.checker.method;

import org.purah.core.checker.PurahMethod;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ByAnnMethodChecker extends AbstractMethodToChecker {

    Class<?> annClazz;


    public ByAnnMethodChecker(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        this.name = name;
        annClazz = method.getParameters()[0].getType();
        String errorMsg = errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {
        Annotation annotation = inputToCheckerArg.annOnField(annClazz);
        Object[] args = new Object[2];
        args[0] = annotation;
        args[1] = inputToCheckerArg;
//                purahEnableMethod.inputArgValue(inputToCheckerArg);
        return purahEnableMethod.invokeResult(args);


    }

    @Override
    public PurahMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahMethod(methodsToCheckersBean, method, 1);
    }

    public static String errorMsgCheckerByAnnMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 2) {
            return "入参必須有兩個参数" + method;
        }
        Class<?> parameterizedType = method.getParameters()[0].getType();

        if (!(parameterizedType.isAnnotation())) {
            return "第一個參數必須為注解，將被填充為字段上的注解值";
        }
        return null;


    }

}
