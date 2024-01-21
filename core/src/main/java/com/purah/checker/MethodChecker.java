package com.purah.checker;

import com.purah.base.Name;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;

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



    public MethodChecker(Object methodsToCheckersBean, Method method) {
        this.name = method.getAnnotation(Name.class).value();
        this.method = method;
        this.methodsToCheckersBean = methodsToCheckersBean;
        this.resultClass = method.getReturnType();
        if ((!this.resultClass.equals(Boolean.class)) && (!this.resultClass.equals(boolean.class))) {

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
            throw new RuntimeException(e);
        }
    }
}