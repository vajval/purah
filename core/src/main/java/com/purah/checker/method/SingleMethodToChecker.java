package com.purah.checker.method;

import com.google.common.collect.Lists;
import com.purah.base.Name;
import com.purah.base.PurahEnableMethod;
import com.purah.base.PurahEnableMethodValidator;
import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static com.purah.base.PurahEnableMethodValidator.methodToCheckerValidator;

public class SingleMethodToChecker extends MethodToChecker {

    //    PurahEnableMethodValidator purahEnableMethodValidator = new PurahEnableMethodValidator(Lists.newArrayList(Name.class), Lists.newArrayList(Object.class));


    public SingleMethodToChecker(Object methodsToCheckersBean, Method method) {
        super(methodsToCheckersBean, method);

    }

    @Override
    protected void init() {
        super.init();

        this.name = method.getAnnotation(Name.class).value();




    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {
        return purahEnableMethod.invoke(checkInstance);
//        Object inputArg = checkInstance;
//        if (!argIsCheckInstanceClass) {
//            inputArg = checkInstance.instance();
//        }
//        try {
//
//            Object result = method.invoke(methodsToCheckersBean, inputArg);
//            if (resultIsCheckResultClass) {
//                return (CheckerResult) result;
//            } else {
//                Boolean resultValue = (Boolean) result;
//                if (resultValue) {
//                    return SingleCheckerResult.success(true, "success");
//                } else {
//                    return SingleCheckerResult.failed(false, "failed");
//
//                }
//            }
//
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
    }

    public static boolean enable(Object methodsToCheckersBean, Method method) {
        return methodToCheckerValidator.enable(methodsToCheckersBean, method);
    }

    @Override
    protected String errorMsg(Object methodsToCheckersBean, Method method) {
        return staticErrorMsg(methodsToCheckersBean, method);
    }

    protected static String staticErrorMsg(Object methodsToCheckersBean, Method method) {
        Name nameAnnotation = method.getAnnotation(Name.class);
        if (nameAnnotation == null) {
            return "必须要给规则一个名字 请在对应method上增加 @Name注解" + method;
        }
        if (method.getParameters().length != 1) {
            return "入参只能有一个参数" + method;
        }
        Class<?> returnType = method.getReturnType();
        if (!(CheckerResult.class.isAssignableFrom(returnType)) &&

                !(boolean.class.isAssignableFrom(returnType))) {
            return "返回值必须是 CheckerResult  或者 boolean " + method;

        }
        return null;
    }

}
