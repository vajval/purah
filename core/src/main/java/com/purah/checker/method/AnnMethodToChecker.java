package com.purah.checker.method;

import com.purah.base.Name;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.checker.custom.AbstractCustomAnnChecker;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

public class AnnMethodToChecker extends MethodToChecker {

    public AnnMethodToChecker(Object methodsToCheckersBean, Method method, String useName) {

        super(methodsToCheckersBean, method);
        this.useName = useName;
    }

    String useName;
    Class<?> annClazz;

    @Override
    protected void init() {
        super.init();


        Parameter[] parameters = method.getParameters();
        annClazz = parameters[0].getType();
        inputCheckInstanceClass = parameters[1].getType();

        if (CheckInstance.class.isAssignableFrom(parameters[1].getType())) {
            argIsCheckInstanceClass = true;
            inputCheckInstanceClass = (Class<?>) ((ParameterizedType) method.getGenericParameterTypes()[1]).getActualTypeArguments()[0];
        }


    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {

        Annotation annotation = checkInstance.annOf(annClazz);

        try {
            Object result;
            if (argIsCheckInstanceClass) {
                result = method.invoke(methodsToCheckersBean, annotation, checkInstance);
            } else {
                result = method.invoke(methodsToCheckersBean, annotation, checkInstance.instance());
            }
            if (resultIsCheckResultClass) {
                return (CheckerResult) result;
            } else {
                boolean success = (boolean) result;
                if (success) return SingleCheckerResult.success();
                else {
                    return SingleCheckerResult.failed("failed", "failed");
                }
            }


        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected String errorMsg(Object methodsToCheckersBean, Method method) {

        if (method.getParameters().length != 2) {
            return "入参必须有2个参数，第一个注解，第二个需要检测的对象或者附带对象的 checkInstance" + method;
        }
        Class<?> returnType = method.getReturnType();
        if (!(CheckerResult.class.isAssignableFrom(returnType)) &&

                !(boolean.class.isAssignableFrom(returnType))) {
            return "返回值必须是 CheckerResult  或者 boolean " + method;

        }
        return null;
    }

    @Override
    public String name() {
        return useName;
    }
}
