package org.purah.core.checker.method;


import org.purah.core.checker.base.BaseCheckerWithCache;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;

/**
 * 直接将函数生成规则
 * 如果返回结果是
 */
public abstract class MethodToCheckerWithCache extends BaseCheckerWithCache {


    protected Method method;

    protected Object methodsToCheckersBean;

    protected String name;
    protected PurahEnableMethod purahEnableMethod;


    protected String errorMsgProtected(Object methodsToCheckersBean, Method method) {
        return validator().errorMsg(methodsToCheckersBean, method);
    }


    public MethodToCheckerWithCache(Object methodsToCheckersBean, Method method) {
        String errorMsg = errorMsgProtected(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

        purahEnableMethod = purahEnableMethod(methodsToCheckersBean, method);

        this.methodsToCheckersBean = methodsToCheckersBean;
        this.method = method;
        this.init();
    }

    public abstract PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method);

    protected void init() {


    }

    protected abstract PurahEnableMethodValidator validator();


    @Override
    public String name() {
        return name;
    }


    @Override
    public Class<?> inputCheckInstanceClass() {
        return purahEnableMethod.needCheckArgClass();
    }

    @Override
    public Class<?> resultClass() {
        return purahEnableMethod.resultWrapperClass();
    }

    @Override
    public abstract CheckResult doCheck(CheckInstance checkInstance);


    @Override
    public String logicFrom() {

        return method.toGenericString();
    }
}