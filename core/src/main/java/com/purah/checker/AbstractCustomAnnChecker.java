package com.purah.checker;

import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class AbstractCustomAnnChecker extends BaseChecker {

    Map<Class<? extends Annotation>, BiFunction<Annotation, CheckInstance, CheckerResult>> map = new HashMap<>();




    private void initMethods() {

        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            if (!CheckerResult.class.isAssignableFrom(declaredMethod.getReturnType())) {
                continue;
            }
            Parameter[] parameters = declaredMethod.getParameters();
            if (parameters.length != 2) continue;

            Class<?> annClazz = parameters[0].getType();
            if (!Annotation.class.isAssignableFrom(annClazz)) {
                continue;
            }
            if (!CheckInstance.class.isAssignableFrom(parameters[1].getType())) {
                continue;
            }
            map.put((Class<? extends Annotation>) annClazz, (a, b) -> {
                try {
                    return (CheckerResult) declaredMethod.invoke(this, a, b);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });

        }

    }


    public AbstractCustomAnnChecker() {
        initMethods();
    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {
        List<Annotation> annotations = ((CheckInstance<?>) checkInstance).getAnnotations();
        for (Annotation annotation : annotations) {
            BiFunction<Annotation, CheckInstance, CheckerResult> biFunction = map.get(annotation.annotationType());
            if (biFunction == null) continue;
            CheckerResult checkerResult = biFunction.apply(annotation, checkInstance);

            if (checkerResult.isFailed()) {
                return checkerResult;
            }

        }

        return SingleCheckerResult.success();
    }


//    public CheckerResult cnPhoneNum(CNPhoneNum cnPhoneNum, CheckInstance<String> str) {
//        String strValue = str.instance();
//        //gpt 说的
//        if (strValue.matches("^1[3456789]\\d{9}$")) {
//            return success("正确的");
//        }
//        return failed(cnPhoneNum.errorMsg());
//
//
//    }

}