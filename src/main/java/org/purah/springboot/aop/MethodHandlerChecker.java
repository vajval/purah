package org.purah.springboot.aop;

import com.google.common.collect.Lists;

import org.purah.core.PurahContext;
import org.purah.core.checker.BaseChecker;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.Checker;
import org.purah.core.checker.ExecChecker;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.combinatorial.MultiCheckerExecutor;
import org.purah.core.checker.result.*;
import org.purah.springboot.ann.CheckIt;
import org.purah.springboot.ann.FillToMethodResult;
import org.purah.springboot.result.ArgCheckResult;
import org.purah.springboot.result.MethodCheckResult;

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
    protected Object methodsToCheckersBean;
    protected Method method;
    protected Type returnType;


    public MethodHandlerChecker(Object methodsToCheckersBean, Method method, PurahContext purahContext) {
        this.purahContext = purahContext;
        this.methodsToCheckersBean = methodsToCheckersBean;
        this.method = method;
        this.returnType = method.getGenericReturnType();
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


    @Override
    public MethodCheckResult doCheck(CheckInstance checkInstance) {
        Object[] args = (Object[]) checkInstance.instance();
        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(ExecType.Main.all_success, ResultLevel.failedIgnoreMatch);
        List<Supplier<CheckResult>> execList = new ArrayList<>();

        for (MethodArgCheckConfig methodArgCheckConfig : methodArgCheckConfigList) {
            execList.add(() -> this.checkSingleArgByConfig(methodArgCheckConfig, args[methodArgCheckConfig.index]));

        }
        multiCheckerExecutor.exec(execList);
        String log = methodsToCheckersBean.getClass() + "|method:" + method.getName();
        MultiCheckResult<ArgCheckResult> multiCheckResult = (MultiCheckResult) multiCheckerExecutor.multiCheckResult(log);


        return new MethodCheckResult(
                multiCheckResult.mainCheckResult(),
                multiCheckResult.value(), methodsToCheckersBean, method
        );


    }


    private ArgCheckResult checkSingleArgByConfig(MethodArgCheckConfig methodArgCheckConfig, Object checkArg) {

        CheckIt checkIt = methodArgCheckConfig.checkItAnn;


        MultiCheckerExecutor executor = new MultiCheckerExecutor(checkIt.execType(), checkIt.resultLevel());


        List<Supplier<CheckResult>> execList = new ArrayList<>();
        System.out.println();

        List<? extends ExecChecker<?, ?>> checkerList = methodArgCheckConfig.checkerNameList.stream().map(i -> purahContext.checkManager().get(i)).collect(Collectors.toList());

        for (Checker checker : checkerList) {
            execList.add(() -> checker.check(CheckInstance.create(checkArg)));
        }

        executor.exec(execList);
        String log = "method:" + method.getName() + "|arg" + methodArgCheckConfig.index;


        MultiCheckResult<CheckResult> multiCheckResult = executor.multiCheckResult(log);


        return ArgCheckResult.create(multiCheckResult.mainCheckResult(), methodArgCheckConfig.checkerNameList,
                multiCheckResult.value(),
                checkIt, checkArg);


    }


    public Object fillObject(MethodCheckResult methodCheckResult) {
        System.out.println(returnType);
        if (this.isMethodCheckResultType()) {
            return methodCheckResult;
        } else if (this.isArgCheckResultType()) {
            return methodCheckResult.value().get(0);
        } else if (this.isCombinatorialCheckerResultType()) {
            return CombinatorialCheckResult.create(methodCheckResult, ResultLevel.all);

        } else if (this.isSingleResultType()) {
            return methodCheckResult.mainCheckResult();
        } else if (this.isBooleanResultType()) {
            return methodCheckResult.isSuccess();
        } else {
            throw new RuntimeException("asd");
        }
    }

    private boolean isArgCheckResultType() {
        return ArgCheckResult.class.equals(returnType);
    }

    private boolean isMethodCheckResultType() {
        return MethodCheckResult.class.equals(returnType) || CheckResult.class.equals(returnType);
    }

    private boolean isSingleResultType() {
        return SingleCheckResult.class.equals(returnType);
    }

    private boolean isBooleanResultType() {
        return boolean.class.equals(returnType);
    }

    private boolean isCombinatorialCheckerResultType() {
        return CombinatorialCheckResult.class.equals(returnType);
    }

    public boolean isFillToMethodResult() {
        return fillToMethodResult;
    }


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