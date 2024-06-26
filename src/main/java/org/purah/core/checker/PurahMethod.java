package org.purah.core.checker;

import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;


public class PurahMethod {

    protected Method method;

    protected Object bean;


    protected boolean methodParamBeWrapped;
    protected Class<?> inputArgValueClazz = Object.class;
    protected int needCheckArgIndex;


    protected boolean methodResultBeWrapped;
    protected Class<?> resultDataClazz = boolean.class;


    public PurahMethod(Object bean, Method method) {
        this(bean, method, 0);
    }

    public PurahMethod(Object bean, Method method, int needCheckArgIndex) {
        this.method = method;
        this.bean = bean;

        this.methodParamBeWrapped = false;
        this.needCheckArgIndex = needCheckArgIndex;
        if (needCheckArgIndex != -1) {
            this.inputArgValueClazz = method.getParameterTypes()[needCheckArgIndex];
            if (this.inputArgValueClazz.equals(InputToCheckerArg.class)) {
                this.inputArgValueClazz = inputArgWrapperValueClazz(method, needCheckArgIndex);
                this.methodParamBeWrapped = true;
            }
        }


        this.methodResultBeWrapped = false;
        Type returnType = method.getGenericReturnType();
        if (returnType.equals(boolean.class)) {
            return;
        }

        this.resultDataClazz = resultWrapperDataClazz(method);
        if (this.resultDataClazz == null) {
            throw new RuntimeException();
        }
        this.methodResultBeWrapped = true;
    }

    public static Class<?> resultWrapperDataClazz(Method method) {
        Type returnType = method.getGenericReturnType();
        ResolvableType[] generics = ResolvableType.forType(returnType).as(CheckResult.class).getGenerics();
        if (generics.length != 0) {
            Class<?> resolve = generics[0].resolve();
            if (resolve != null) {
                return resolve;
            }
            return Object.class;
        }
        return null;

    }

    public static Class<?> inputArgWrapperValueClazz(Method method, int needCheckArgIndex) {
        ParameterizedType genericReturnType = (ParameterizedType) method.getGenericParameterTypes()[needCheckArgIndex];
        return (Class) genericReturnType.getActualTypeArguments()[0];
    }


    public CheckResult invokeResult(InputToCheckerArg inputToCheckerArg, Object[] invokeArgs) {
        Object result;
        if (this.needCheckArgIndex != -1 && !methodParamBeWrapped) {
            invokeArgs[this.needCheckArgIndex] = ((InputToCheckerArg) invokeArgs[this.needCheckArgIndex]).argValue();

        }
        result = invoke(invokeArgs);


        if (methodResultBeWrapped) {
            return (CheckResult) result;
        } else if (Objects.equals(result, true)) {
            return BaseLogicCheckResult.successBuildLog(inputToCheckerArg, true);
        } else if (Objects.equals(result, false)) {
            return BaseLogicCheckResult.failedBuildLog(inputToCheckerArg, false);
        }

        throw new RuntimeException("bugaichucuo");

    }

    private Object invoke(Object[] args) {
        try {
            return method.invoke(bean, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public String logicFrom() {
        return method.toGenericString();
    }

    public Class<?> needCheckArgClass() {
        return inputArgValueClazz;
    }

    public Class<?> resultDataClass() {
        return resultDataClazz;

    }


}
