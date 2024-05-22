package org.purah.springboot.aop;

import com.google.common.collect.Lists;

import org.purah.core.PurahContext;
import org.purah.core.checker.BaseChecker;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.Checker;
import org.purah.core.checker.ExecChecker;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.combinatorial.MultiCheckerExecutor;
import org.purah.core.checker.result.CheckerResult;
import org.purah.core.checker.result.CombinatorialCheckerResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.springboot.ann.CheckIt;
import org.purah.springboot.ann.FillToMethodResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodHandlerChecker extends BaseChecker {

    protected PurahContext purahContext;
    protected List<MethodArgCheckConfig> methodArgCheckConfigList;

    protected boolean fillToMethodResult;
    Object methodsToCheckersBean;
    Method method;
    boolean resultIsCheckResultClass = false;


    public MethodHandlerChecker(Object methodsToCheckersBean, Method method, PurahContext purahContext) {
        this.purahContext = purahContext;
        this.methodsToCheckersBean = methodsToCheckersBean;
        this.method = method;
        Type returnType = method.getGenericReturnType();
        if (!returnType.equals(boolean.class)) {
            resultIsCheckResultClass = true;
        }

        this.init();

    }


    //    @Override
    protected void init() {
        ArrayList<Parameter> parameterArrayList = Lists.newArrayList(method.getParameters());
        methodArgCheckConfigList = new ArrayList<>();

        int index = -1;
        for (Parameter parameter : parameterArrayList) {
            CheckIt checkIt = parameter.getAnnotation(CheckIt.class);
            index++;
            if (checkIt == null) continue;
            /*
             * 找到有注解的参数
             */

            List<String> useCheckerNameList = Stream.of(checkIt.value()).collect(Collectors.toList());
            Class<?> argClazz = parameter.getType();
            if (useCheckerNameList.size() == 0) {

                CheckIt argClazzCheckIt = argClazz.getDeclaredAnnotation(CheckIt.class);
                if (argClazzCheckIt == null) continue;
                useCheckerNameList = Stream.of(argClazzCheckIt.value()).collect(Collectors.toList());

            }
            if (useCheckerNameList.size() == 0) continue;


            MethodArgCheckConfig methodArgCheckConfig = new MethodArgCheckConfig();

            methodArgCheckConfig.setCheckItAnn(checkIt);
            methodArgCheckConfig.setClazz(argClazz);
            methodArgCheckConfig.setIndex(index);
            methodArgCheckConfig.setCheckerNameList(useCheckerNameList);

            methodArgCheckConfigList.add(methodArgCheckConfig);
        }
        FillToMethodResult ann = method.getAnnotation(FillToMethodResult.class);
        fillToMethodResult = (ann != null);

    }


    public boolean isFillToMethodResult() {
        return fillToMethodResult;
    }


    public boolean resultIsCheckResultClass() {
        return resultIsCheckResultClass;
    }

//
//    @Override
//    public CheckerResult check(CheckInstance checkInstance) {
//        CheckerResult check = super.check(checkInstance);
//
//        CombinatorialCheckerResult result = new CombinatorialCheckerResult();
//        result.addResult(check);
//        result.setLogicFromByChecker(check.logicFrom());
//
//        return result;
//    }

    @Override
    public CombinatorialCheckerResult doCheck(CheckInstance checkInstance) {
        Object[] args = (Object[]) checkInstance.instance();


        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(ExecType.Main.all_success, ResultLevel.failedIgnoreMatch);


        List<Supplier<CheckerResult>> execList = new ArrayList<>();

        for (MethodArgCheckConfig methodArgCheckConfig : methodArgCheckConfigList) {
            execList.add(() -> this.checkSingleArgByConfig(methodArgCheckConfig, args[methodArgCheckConfig.index]));

        }
        multiCheckerExecutor.exec(execList);
        String log = methodsToCheckersBean.getClass() + "|method:" + method.getName();
        return multiCheckerExecutor.result(log);
    }

    private CombinatorialCheckerResult checkSingleArgByConfig(MethodArgCheckConfig methodArgCheckConfig, Object arg) {
        ExecType.Main execType = methodArgCheckConfig.checkItAnn.execType();
        ResultLevel resultLevel = methodArgCheckConfig.checkItAnn.ignoreSuccessResult();
        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(execType, resultLevel);
        List<Supplier<CheckerResult>> execList = new ArrayList<>();


        List<? extends ExecChecker<?, ?>> checkerList = methodArgCheckConfig.checkerNameList.stream().map(i -> purahContext.checkManager().get(i)).collect(Collectors.toList());


        for (Checker checker : checkerList) {
            execList.add(() -> checker.check(CheckInstance.create(arg)));
        }
        multiCheckerExecutor.exec(execList);
        String log = "method:" + method.getName() + "|arg" + methodArgCheckConfig.index;
        return multiCheckerExecutor.result(log);

    }

    private List<CheckerResult> checkArgByConfig(MethodArgCheckConfig methodArgCheckConfig, Object arg) {
        List<CheckerResult> resultList = new ArrayList<>();

        List<? extends ExecChecker<?, ?>> checkerList = methodArgCheckConfig.checkerNameList.stream().map(i -> purahContext.checkManager().get(i)).collect(Collectors.toList());
        for (Checker checker : checkerList) {
            CheckerResult ruleResult = checker.check(CheckInstance.create(arg));
            resultList.add(ruleResult);
            if (ruleResult.isFailed()) return resultList;

        }
        return resultList;
    }


    protected static String staticErrorMsg(Object methodsToCheckersBean, Method method) {


        FillToMethodResult ann = method.getAnnotation(FillToMethodResult.class);
        if (ann != null) {
            Class<?> returnType = method.getReturnType();
            if (!(CheckerResult.class.isAssignableFrom(returnType)) &&
                    !(boolean.class.isAssignableFrom(returnType))) {
                return "返回值必须是 CheckerResult  或者 boolean " + method;

            }
        }

        return null;
    }

}
