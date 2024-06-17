package org.purah.core.checker.method;


import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.checker.PurahMethod;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;

/**
 * 直接将函数生成规则
 * 如果返回结果是
 */
public abstract class AbstractMethodToChecker extends AbstractBaseSupportCacheChecker {


    protected PurahMethod purahEnableMethod;

    protected String name;
    protected Method method;
    public AbstractMethodToChecker(Object methodsToCheckersBean, Method method, String name) {
        String errorMsg = errorMsgAbstractMethodToChecker(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

        purahEnableMethod = purahEnableMethod(methodsToCheckersBean, method);
        this.name = name;
        this.method = method;
    }

    protected abstract PurahMethod purahEnableMethod(Object methodsToCheckersBean, Method method);


    public static String errorMsgAbstractMethodToChecker(Object methodsToCheckersBean, Method method) {
        if (method == null) {
            return "不支持null method";
        }


        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            return "非public 不生效" + method.toGenericString();
        }

        boolean isStatic = java.lang.reflect.Modifier.isStatic(method.getModifiers());

        if (!isStatic && methodsToCheckersBean == null) {
            return "非静态函数 bean 不能为null" + method.toGenericString();
        }

        Class<?> returnType = method.getReturnType();
        if (!(CheckResult.class.isAssignableFrom(returnType)) && !(boolean.class.isAssignableFrom(returnType))) {
            return "返回值必须是 CheckResult  或者 boolean " + method;

        }
        return null;


    }


    @Override
    public abstract CheckResult doCheck(InputToCheckerArg inputToCheckerArg);

    @Override
    public String name() {
        return name;
    }


    @Override
    public Class<?> inputArgClass() {
        return purahEnableMethod.needCheckArgClass();
    }

    @Override
    public Class<?> resultDataClass() {
        return purahEnableMethod.resultDataClass();
    }


    @Override
    public String logicFrom() {
        return purahEnableMethod.logicFrom();
    }


}