package com.purah.checker;

import com.purah.base.Name;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 直接将函数生成规则
 * 如果返回结果是
 */
public class MethodChecker extends BaseChecker {


    Method method;

    Object methodsToCheckersBean;
    String name;
    Class<?> resultClass;

    Class<?> inputCheckInstanceClass;


    boolean argIsCheckInstanceClass = false;
    boolean resultIsCheckResultClass = false;


    public static boolean enable(Object methodsToCheckersBean, Method method) {
        Name nameAnnotation = method.getAnnotation(Name.class);
        if (nameAnnotation == null) {
            return false;
        }
        if (method.getParameters().length != 1) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        if (!(returnType.isAssignableFrom(CheckerResult.class)) &&
                !(returnType.isAssignableFrom(Boolean.class)) &&
                !(returnType.isAssignableFrom(boolean.class))) {
            return false;

        }
        return true;
    }



    public MethodChecker(Object methodsToCheckersBean, Method method) {

        if (enable(methodsToCheckersBean, method)) {

            throw new RuntimeException();
        }

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> inputCheckInstanceClass() {
        return inputCheckInstanceClass;
    }

    @Override
    public Class<?> resultClass() {
        return resultClass;
    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {
        Object inputArg = checkInstance;
        if (!argIsCheckInstanceClass) {
            inputArg = checkInstance.instance();
        }
        try {
            Object result = method.invoke(methodsToCheckersBean, inputArg);
            if (resultIsCheckResultClass) {
                return (CheckerResult) result;
            } else {
                Boolean resultValue = (Boolean) result;
                if (resultValue) {
                    return SingleCheckerResult.success(true, "success");
                } else {
                    return SingleCheckerResult.failed(false, "success");

                }
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}