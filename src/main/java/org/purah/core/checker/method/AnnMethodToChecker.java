package org.purah.core.checker.method;

import com.google.common.collect.Lists;
import org.purah.core.base.PurahEnableMethod;
import org.purah.core.base.PurahEnableMethodValidator;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.result.CheckerResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AnnMethodToChecker extends MethodToChecker {

    Class<?> annClazz;

    public AnnMethodToChecker(Object methodsToCheckersBean, Method method, String name) {

        super(methodsToCheckersBean, method);
        this.name = name;
        Parameter[] parameters = method.getParameters();
        annClazz = parameters[0].getType();
    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {


        Annotation annotation = checkInstance.annOf(annClazz);
        Object[] args = new Object[2];
        args[0] = annotation;

        args[1] = purahEnableMethod.checkInstanceToInputArg(checkInstance);


        return purahEnableMethod.invoke(args);


    }

    @Override
    public PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahEnableMethod(methodsToCheckersBean, method, 1);
    }

    public static PurahEnableMethodValidator annMethodToCheckerValidator = new PurahEnableMethodValidator(Lists.newArrayList(),
            Lists.newArrayList(Annotation.class, Object.class), Lists.newArrayList(boolean.class, CheckerResult.class)
    );

    @Override
    protected PurahEnableMethodValidator validator() {
        return annMethodToCheckerValidator;
    }
}
//        if (method.getParameters().length != 2) {
//            return "入参必须有2个参数，第一个注解，第二个需要检测的对象或者附带对象的 checkInstance" + method;
//        }
//        Class<?> returnType = method.getReturnType();
//        if (!(CheckerResult.class.isAssignableFrom(returnType)) &&
//
//                !(boolean.class.isAssignableFrom(returnType))) {
//            return "返回值必须是 CheckerResult  或者 boolean " + method;
//
//        }
//        return null;
//    }


//        Annotation annotation = checkInstance.annOf(annClazz);
//
//        try {
//            Object result;
//            if (argIsCheckInstanceClass) {
//                result = method.invoke(methodsToCheckersBean, annotation, checkInstance);
//            } else {
//                result = method.invoke(methodsToCheckersBean, annotation, checkInstance.instance());
//            }
//            if (resultIsCheckResultClass) {
//                return (CheckerResult) result;
//            } else {
//                boolean success = (boolean) result;
//                if (success) return SingleCheckerResult.success();
//                else {
//                    return SingleCheckerResult.failed("failed", "failed");
//                }
//            }
//
//
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//}
