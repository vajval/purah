package org.purah.core.checker.method;

import org.purah.core.base.Name;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.springframework.core.ResolvableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class PurahEnableMethod {

    protected Method method;

    protected Object bean;

    protected String name;
    protected Class<?> resultClass = boolean.class;

    protected Class<?> needCheckArgClass;

    protected boolean argIsCheckInstanceClass = false;
    protected boolean resultIsCheckResultClass;


    public PurahEnableMethod(Object bean, Method method) {
        this(bean, method, 0);
    }

    public PurahEnableMethod(Object bean, Method method, int needCheckArgIndex) {
        this.method = method;
        this.bean = bean;
        Name nameAnn = method.getDeclaredAnnotation(Name.class);
        if (nameAnn != null) {
            this.name = nameAnn.value();
        } else {
            this.name = this.method.toGenericString();
        }
        Type returnType = method.getGenericReturnType();
        ResolvableType[] generics = ResolvableType.forType(returnType).as(CheckResult.class).getGenerics();

        if (generics.length != 0) {
            Class<?> resolve = generics[0].resolve();
            resultClass = resolve;
            if (resolve == null) {
                resultClass = Object.class;
            }
            resultIsCheckResultClass = true;
        } else if (returnType.equals(boolean.class)) {
            resultIsCheckResultClass = false;

        } else {
            throw new RuntimeException("返回类型不合适");
        }


        this.needCheckArgClass = method.getParameterTypes()[needCheckArgIndex];
        if (this.needCheckArgClass.equals(InputCheckArg.class)) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericParameterTypes()[needCheckArgIndex];
            this.needCheckArgClass = (Class) genericReturnType.getActualTypeArguments()[0];
            argIsCheckInstanceClass = true;
        }


    }


    public Object checkInstanceToInputArg(InputCheckArg inputCheckArg) {
        if (argIsCheckInstanceClass) {
            return inputCheckArg;
        }
        return inputCheckArg.inputArg();
    }

    public Object invoke(Object[] args) {
        try {

            return method.invoke(bean, args);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean resultIsCheckResultClass() {
        return resultIsCheckResultClass;
    }

    public Method wrapperMethod() {
        return method;
    }

    public Class<?> needCheckArgClass() {
        return needCheckArgClass;
    }

    public Class<?> resultWrapperClass() {
        return resultClass;

    }

    public String name() {
        return name;
    }

    public static boolean validReturnType(Class<?> clazz) {
        return clazz.equals(boolean.class) || CheckResult.class.isAssignableFrom(clazz);
    }
}
