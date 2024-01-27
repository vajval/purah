package com.purah.checker.custom;

import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.ExecChecker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class AbstractCustomAnnChecker extends BaseChecker {

    Map<Class<? extends Annotation>, ExecChecker> map = new HashMap<>();


    private void initMethods() {

        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            if ((!CheckerResult.class.isAssignableFrom(declaredMethod.getReturnType())) && (!declaredMethod.getReturnType().equals(boolean.class))) {
                continue;
            }

            Parameter[] parameters = declaredMethod.getParameters();
            Class<?> annClazz = parameters[0].getType();
            if (parameters.length != 2) continue;


            if (!Annotation.class.isAssignableFrom(annClazz)) {
                continue;
            }

            String name = this.name() + "[" + annClazz.getName() + "]";
            ExecChecker execChecker = map.computeIfAbsent((Class<? extends Annotation>) annClazz, i -> new ExecChecker(name));
            Checker checker = methodToChecker(declaredMethod, name);
            execChecker.addNewChecker(checker);

        }

    }


    public Checker methodToChecker(Method declaredMethod, String useName) {

        Parameter[] parameters = declaredMethod.getParameters();
        Class<?> annClazz = parameters[0].getType();
        Class<?> inputClazz = parameters[1].getType();
        boolean isInstance = false;
        boolean resultIsResult = !declaredMethod.getReturnType().equals(boolean.class);

        if (CheckInstance.class.isAssignableFrom(parameters[1].getType())) {
            isInstance = true;
            inputClazz = (Class<?>) ((ParameterizedType) declaredMethod.getGenericParameterTypes()[1]).getActualTypeArguments()[0];
        }


        AbstractCustomAnnChecker thisChecker = this;
        Class<?> finalInputClazz = inputClazz;
        boolean finalIsInstance = isInstance;
        return new BaseChecker<>() {
            @Override
            public Class<?> inputCheckInstanceClass() {
                return finalInputClazz;
            }

            @Override
            public CheckerResult doCheck(CheckInstance checkInstance) {

                Annotation annotation = checkInstance.annOf(annClazz);
                try {
                    Object result;
                    if (finalIsInstance) {
                        result = declaredMethod.invoke(thisChecker, annotation, checkInstance);
                    } else {
                        result = declaredMethod.invoke(thisChecker, annotation, checkInstance.instance());
                    }
                    if (resultIsResult) {
                        return (CheckerResult) result;
                    } else {
                        boolean success = (boolean) result;
                        if (success) return SingleCheckerResult.success();
                        else {
                            return SingleCheckerResult.failed("failed", "failed");
                        }
                    }


                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public String name() {
                return useName;
            }
        };

    }


    public AbstractCustomAnnChecker() {
        initMethods();
    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {
        List<Annotation> annotations = ((CheckInstance<?>) checkInstance).getAnnotations();
        for (Annotation annotation : annotations) {
            ExecChecker execChecker = map.get(annotation.annotationType());

            if (execChecker == null) continue;
            CheckerResult checkerResult = execChecker.check(checkInstance);

            if (checkerResult.isFailed()) {
                return checkerResult;
            }

        }

        return SingleCheckerResult.success();
    }



}