package com.purah.base;

import com.google.common.collect.Lists;
import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Wrapper;
import java.util.Collections;
import java.util.List;

public class PurahEnableMethod {

    protected Method method;


    protected Object bean;

    protected String name;
    protected Class<?> resultClass = boolean.class;

    protected Class<?> needCheckArgClass;


    protected boolean argIsCheckInstanceClass = false;
    protected boolean resultIsCheckResultClass = false;
    static List<Class<?>> allowReturnClazz = Lists.newArrayList(boolean.class, CheckerResult.class);


    public PurahEnableMethod(Object bean, Method method) {
        this(bean, method, 0);
    }

    public PurahEnableMethod(Object bean, Method method, int needCheckArgIndex) {
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

        this.needCheckArgClass = method.getParameterTypes()[needCheckArgIndex];
        if (this.needCheckArgClass.equals(CheckInstance.class)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericParameterTypes()[needCheckArgIndex];
            this.needCheckArgClass = (Class) genericReturnType.getActualTypeArguments()[0];
            argIsCheckInstanceClass = true;
        }
        if (CollectionUtils.isEmpty(allowReturnClazz)) {
            return;
        }

        for (Class<?> allowReturnClazz : allowReturnClazz) {
            if (allowReturnClazz.isAssignableFrom((Class<?>) returnType)) {
                return;
            }

        }
        throw new RuntimeException("返回类型不合适");

    }

//    public Object[] getArgs(CheckInstance checkInstance) {
//        Object[] result = {checkInstance};
//        if (!argIsCheckInstanceClass) {
//            result[0] = checkInstance.instance();
//        }
//        return result;
//    }


    protected boolean argIsCheckInstanceClass() {
        return argIsCheckInstanceClass;
    }

    public Object checkInstanceToInputArg(CheckInstance checkInstance) {
        if (argIsCheckInstanceClass()) {
            return checkInstance;
        }
        return checkInstance.instance();
    }

    public CheckerResult invoke(Object[] args) {
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

    public static boolean validReturnType(Class<?> clazz) {
        if (clazz.equals(boolean.class) || CheckerResult.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
}
