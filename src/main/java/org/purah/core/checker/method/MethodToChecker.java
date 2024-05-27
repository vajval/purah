package org.purah.core.checker.method;



import org.purah.core.base.PurahEnableMethod;
import org.purah.core.base.PurahEnableMethodValidator;
import org.purah.core.checker.BaseChecker;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;

/**
 * 直接将函数生成规则
 * 如果返回结果是
 */
public abstract class MethodToChecker extends BaseChecker {


    protected Method method;

    protected Object methodsToCheckersBean;

    protected String name;
    protected Class<?> resultClass = boolean.class;

    protected boolean resultIsCheckResultClass = false;


    protected String errorMsgProtected(Object methodsToCheckersBean, Method method) {
        return validator().errorMsg(methodsToCheckersBean, method);
    }


    PurahEnableMethod purahEnableMethod;

    public MethodToChecker(Object methodsToCheckersBean, Method method) {
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