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
        return errorMsg(methodsToCheckersBean, method) == null;
    }


    private static String errorMsg(Object methodsToCheckersBean, Method method) {
        Name nameAnnotation = method.getAnnotation(Name.class);
        if (nameAnnotation == null) {
            return "必须要给规则一个名字 请在对应method上增加 @Name注解" + method;
        }
        if (method.getParameters().length != 1) {
            return "入参只能有一个参数" + method;
        }
        Class<?> returnType = method.getReturnType();
        if (!(CheckerResult.class.isAssignableFrom(returnType)) &&

                !(boolean.class.isAssignableFrom(returnType))) {
            return "返回值必须是 CheckerResult  或者 boolean " + method;

        }
        return null;
    }

    public MethodChecker(Object methodsToCheckersBean, Method method) {
        String errorMsg = errorMsg(methodsToCheckersBean, method);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

        this.method = method;


        this.name = method.getAnnotation(Name.class).value();
        this.methodsToCheckersBean = methodsToCheckersBean;
        this.resultClass = method.getReturnType();
        if ( !this.resultClass.equals(boolean.class)) {

            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
            resultClass = (Class) genericReturnType.getActualTypeArguments()[0];

            resultIsCheckResultClass = true;
        }
        this.inputCheckInstanceClass = method.getParameterTypes()[0];
        if (this.inputCheckInstanceClass.equals(CheckInstance.class)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericParameterTypes()[0];
            this.inputCheckInstanceClass = (Class) genericReturnType.getActualTypeArguments()[0];
            argIsCheckInstanceClass = true;
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}