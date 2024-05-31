package org.purah.core.checker.method.toChecker;


import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;

/**
 * 直接将函数生成规则
 * 如果返回结果是
 */
public abstract class AbstractMethodToChecker extends BaseSupportCacheChecker {


    protected Method method;
    protected Object methodsToCheckersBean;

    protected String name;
    protected PurahEnableMethod purahEnableMethod;


    protected String errorMsgProtected(Object methodsToCheckersBean, Method method) {
        return validator().errorMsg(methodsToCheckersBean, method);
    }


    public AbstractMethodToChecker(Object methodsToCheckersBean, Method method) {
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