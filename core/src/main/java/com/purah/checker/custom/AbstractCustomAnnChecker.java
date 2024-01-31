package com.purah.checker.custom;

import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.ExecChecker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.checker.method.AnnMethodToChecker;
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


    protected void initMethods() {

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
            AnnMethodToChecker annMethodToChecker = new AnnMethodToChecker(this, declaredMethod, name);
            execChecker.addNewChecker(annMethodToChecker);

        }

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