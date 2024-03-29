package com.purah.checker.custom;

import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.ExecChecker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.checker.method.AnnMethodToChecker;
import org.checkerframework.checker.units.qual.C;
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
    /**
     * 过滤出能用的函数
     * 第一个 参数 是自定义的注解
     * 第二个是要检查的对象 arg
     * 返回值只能是boolean或者 CheckerResult.class
     * ExecChecker 会对不同class 的入参 传递到指定的的函数中
     *
     * 例如写了两个
     *
     * rangeLong ( Range range ,Long num)
     *
     * rangeInteger ( Range range ,CheckInstance<Integer> num)
     *
     * 需要检验的参数是Long类型会总动选择rangeLong
     * 需要检验的参数是Integer类型会总动选择rangeInteger
     * CheckInstance 没写泛型的算Object 类型
     *
     */



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
        CombinatorialCheckerResult result = new CombinatorialCheckerResult();
        for (Annotation annotation : annotations) {
            ExecChecker execChecker = map.get(annotation.annotationType());

            if (execChecker == null) continue;
            CheckerResult checkerResult = execChecker.check(checkInstance);

            result.addResult(checkerResult);


            if (result.isFailed()) {
                return result;
            }

        }


        return result;
    }


}