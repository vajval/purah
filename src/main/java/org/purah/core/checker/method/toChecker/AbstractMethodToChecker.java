package org.purah.core.checker.method.toChecker;


import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;

/**
 * 直接将函数生成规则
 * 如果返回结果是
 */
public abstract class AbstractMethodToChecker extends BaseSupportCacheChecker {


    protected PurahEnableMethod purahEnableMethod;


    public AbstractMethodToChecker(Object methodsToCheckersBean, Method method) {
        String errorMsg = validReturnErrorMsg(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

        purahEnableMethod = purahEnableMethod(methodsToCheckersBean, method);

    }

    protected abstract PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method);


    protected abstract String validReturnErrorMsg(Object methodsToCheckersBean, Method method);


    @Override
    public abstract CheckResult doCheck(CheckInstance checkInstance);

    @Override
    public abstract String name();



    @Override
    public Class<?> inputCheckInstanceClass() {
        return purahEnableMethod.needCheckArgClass();
    }

    @Override
    public Class<?> resultClass() {
        return purahEnableMethod.resultWrapperClass();
    }


    @Override
    public String logicFrom() {
        return purahEnableMethod.wrapperMethod().toGenericString();
    }




}