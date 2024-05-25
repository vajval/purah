package org.purah.core.checker.custom;


import org.purah.core.checker.BaseChecker;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.ExecChecker;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.combinatorial.MultiCheckerExecutor;
import org.purah.core.checker.result.*;
import org.purah.core.checker.method.AnnMethodToChecker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AbstractCustomAnnChecker extends BaseChecker {

    Map<Class<? extends Annotation>, ExecChecker> map = new HashMap<>();

    /**
     * 过滤出能用的函数
     * 第一个 参数 是自定义的注解
     * 第二个是要检查的对象 arg
     * 返回值只能是boolean或者 CheckerResult.class
     * ExecChecker 会对不同class 的入参 传递到指定的的函数中
     * <p>
     * 例如写了两个
     * <p>
     * rangeLong ( Range range ,Long num)
     * <p>
     * rangeInteger ( Range range ,CheckInstance<Integer> num)
     * <p>
     * 需要检验的参数是Long类型会总动选择rangeLong
     * 需要检验的参数是Integer类型会总动选择rangeInteger
     * CheckInstance 没写泛型的算Object 类型
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


        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(ExecType.Main.all_success, ResultLevel.failedIgnoreMatch);

        List<Supplier<CheckerResult>> ruleResultSupplierList = new ArrayList<>();


        for (Annotation annotation : annotations) {
            ExecChecker execChecker = map.get(annotation.annotationType());
            if (execChecker == null) continue;
            ruleResultSupplierList.add(() -> execChecker.check(checkInstance));
        }

        multiCheckerExecutor.exec(ruleResultSupplierList);
        String log = "[root." + checkInstance.fieldStr() + "]: " + this.name();
        return multiCheckerExecutor.toCombinatorialCheckerResult(log);


    }


}