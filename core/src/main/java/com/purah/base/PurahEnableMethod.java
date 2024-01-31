package com.purah.base;

import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Wrapper;

public class PurahEnableMethod {

    protected Method method;


    protected Object bean;

    protected String name;
    protected Class<?> resultClass = boolean.class;

    protected Class<?> needCheckArgClass;


    protected boolean argIsCheckInstanceClass = false;
    protected boolean resultIsCheckResultClass = false;

    public PurahEnableMethod(Object bean, Method method) {
        this.method = method;
        this.bean = bean;
        Type returnType = method.getGenericReturnType();
        if (!returnType.equals(boolean.class)) {
            if (returnType.getClass().equals(Class.class)) {
                resultClass = Object.class;
            } else {
                resultClass = (Class) ((ParameterizedType) returnType).getActualTypeArguments()[0];
            }
            resultIsCheckResultClass = true;
        }

        this.needCheckArgClass = method.getParameterTypes()[0];
        if (this.needCheckArgClass.equals(CheckInstance.class)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericParameterTypes()[0];
            this.needCheckArgClass = (Class) genericReturnType.getActualTypeArguments()[0];
            argIsCheckInstanceClass = true;
        }

    }

    public Object[] getArgs(CheckInstance checkInstance) {
        Object[] result = {checkInstance};
        if (!argIsCheckInstanceClass) {
            result[0] = checkInstance.instance();
        }
        return result;
    }

    public CheckerResult invoke(CheckInstance checkInstance) {
        Object[] args = getArgs(checkInstance);
        try {

            Object result = method.invoke(bean, args);
            if (resultIsCheckResultClass) {
                return (CheckerResult) result;
            } else {
                Boolean resultValue = (Boolean) result;
                if (resultValue) {
                    return SingleCheckerResult.success(true, "success");
                } else {
                    return SingleCheckerResult.failed(false, "failed");

                }
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Class<?> needCheckArgClass() {
        return needCheckArgClass;
    }

    public Class<?> resultWrapperClass() {
        return resultClass;

    }
}
