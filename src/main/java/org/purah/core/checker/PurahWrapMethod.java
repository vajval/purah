package org.purah.core.checker;

import org.purah.core.checker.result.LogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.exception.init.InitCheckerException;
import org.springframework.core.ResolvableType;

import java.lang.reflect.*;
import java.util.Objects;


/*
 * wrap method
 * boolean|CheckResult<?>  testMethod(Object|InputToCheckerArg<?> inputArg);
 */
public class PurahWrapMethod {

    protected final Method method;

    protected final Object bean;


    protected boolean methodParamBeWrapped;
    protected Class<?> inputArgValueClazz = Object.class;
    protected final int needCheckArgIndex;


    protected boolean methodResultBeWrapped;
    protected Class<?> resultDataClazz = boolean.class;


    public PurahWrapMethod(Object bean, Method method) {
        this(bean, method, 0);
    }

    public PurahWrapMethod(Object bean, Method method, int needCheckArgIndex) {
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
            throw new UnexpectedException("result class cannot be null");
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


    public CheckResult<Object> invokeResult(InputToCheckerArg<?> inputToCheckerArg, Object[] invokeArgs) {
        Object result;
        if (this.needCheckArgIndex != -1 && !methodParamBeWrapped) {
            invokeArgs[this.needCheckArgIndex] = ((InputToCheckerArg) invokeArgs[this.needCheckArgIndex]).argValue();

        }

        result = invoke(invokeArgs);


        if (methodResultBeWrapped) {
            return (CheckResult) result;
        } else if (Objects.equals(result, true)) {
            return LogicCheckResult.successBuildLog(inputToCheckerArg, true);
        } else if (Objects.equals(result, false)) {
            return LogicCheckResult.failedBuildLog(inputToCheckerArg, false);
        }

        throw new UnexpectedException();

    }

    private Object invoke(Object[] args) {
        try {
            return method.invoke(bean, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new UnexpectedException(e);
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
