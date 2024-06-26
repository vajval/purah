package org.purah.core.checker;


import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractCustomAnnChecker extends AbstractBaseSupportCacheChecker {

    Map<Class<? extends Annotation>, GenericsProxyChecker> map = new ConcurrentHashMap<>();

    ExecType.Main mainExecType;

    ResultLevel resultLevel;

    public AbstractCustomAnnChecker(ExecType.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;
        initMethods();
    }


    /**
     * 过滤出能用的函数
     * 第一个 参数 是自定义的注解
     * 第二个是要检查的对象 arg
     * 返回值只能是boolean或者 CheckResult.class
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
            if ((!CheckResult.class.isAssignableFrom(declaredMethod.getReturnType())) && (!declaredMethod.getReturnType().equals(boolean.class))) {
                continue;
            }

            Parameter[] parameters = declaredMethod.getParameters();
            Class<?> annClazz = parameters[0].getType();
            if (parameters.length != 2) continue;


            if (!Annotation.class.isAssignableFrom(annClazz)) {
                continue;
            }

            String name = this.name() + "[" + annClazz.getName() + "]";
            Checker<?, ?> checkerByAnnMethod = methodToChecker(this, declaredMethod, name);

            GenericsProxyChecker genericsProxyChecker = map.computeIfAbsent((Class<? extends Annotation>) annClazz, i -> GenericsProxyChecker.create(name).addNewChecker(checkerByAnnMethod));

            genericsProxyChecker.addNewChecker(checkerByAnnMethod);

        }

    }


    @Override
    public CheckResult<?> doCheck(InputToCheckerArg inputToCheckerArg) {

        List<Annotation> enableAnnotations = ((InputToCheckerArg<?>) inputToCheckerArg).annListOnField().stream().filter(i -> map.containsKey(i.annotationType())).collect(Collectors.toList());


        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(mainExecType, resultLevel);

        for (Annotation enableAnnotation : enableAnnotations) {
            multiCheckerExecutor.add(() -> map.get(enableAnnotation.annotationType()).check(inputToCheckerArg));
        }
        String annListLogStr = enableAnnotations.stream().map(i -> i.annotationType().getSimpleName()).collect(Collectors.joining(",", "[", "]"));


        String log = inputToCheckerArg.fieldStr() + "  @Ann:" + annListLogStr + " : " + this.name();


        return multiCheckerExecutor.toMultiCheckResult(log);


    }


    public abstract Checker methodToChecker(Object methodsToCheckersBean, Method method, String name);

}