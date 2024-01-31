package com.purah.checker.method;

import com.purah.base.Name;
import com.purah.base.PurahEnableMethod;
import com.purah.base.PurahEnableMethodValidator;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

//        Type returnType = method.getGenericReturnType();
//        if (!returnType.equals(boolean.class)) {
//            if (returnType.getClass().equals(Class.class)) {
//                resultClass = Object.class;
//            } else {
//                resultClass = (Class) ((ParameterizedType) returnType).getActualTypeArguments()[0];
//            }
//            resultIsCheckResultClass = true;
//        }


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
    public abstract CheckerResult doCheck(CheckInstance checkInstance);


    @Override
    public String logicFrom() {

        return method.toGenericString();
    }
}