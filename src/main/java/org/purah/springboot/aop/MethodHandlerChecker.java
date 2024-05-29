package org.purah.springboot.aop;

import com.google.common.collect.Lists;

import org.purah.core.PurahContext;
import org.purah.core.base.Name;
import org.purah.core.checker.base.BaseChecker;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.ExecChecker;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.base.MultiCheckerExecutor;
import org.purah.core.checker.result.*;
import org.purah.springboot.ann.CheckIt;
import org.purah.springboot.ann.FillToMethodResult;
import org.purah.springboot.ann.MethodCheckExecType;
import org.purah.springboot.result.ArgCheckResult;
import org.purah.springboot.result.AutoFillCheckResult;
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
    protected String name;

    protected PurahContext purahContext;
    protected List<MethodArgCheckConfig> methodArgCheckConfigList;
    protected boolean fillToMethodResult;
    protected Object methodsToCheckersBean;

    protected Method method;
    protected Type returnType;
    protected ExecType.Main methodExecType = ExecType.Main.all_success;

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
             * 如果指定了校验规则就按照指定的
             */

            List<String> useCheckerNameList = Stream.of(checkIt.value()).collect(Collectors.toList());
            Class<?> argClazz = parameter.getType();

            //如果没有指定就看class上面的
            if (useCheckerNameList.size() == 0) {

                CheckIt argClazzCheckIt = argClazz.getDeclaredAnnotation(CheckIt.class);
                if (argClazzCheckIt == null) continue;
                useCheckerNameList = Stream.of(argClazzCheckIt.value()).collect(Collectors.toList());

            }
            //都没有就是不检测
            if (useCheckerNameList.size() == 0) continue;


            MethodArgCheckConfig methodArgCheckConfig = new MethodArgCheckConfig();

            methodArgCheckConfig.setCheckItAnn(checkIt);
            methodArgCheckConfig.setClazz(argClazz);
            methodArgCheckConfig.setIndex(index);
            methodArgCheckConfig.setCheckerNameList(useCheckerNameList);

            methodArgCheckConfigList.add(methodArgCheckConfig);
        }
        //都没有就是不检测
        FillToMethodResult ann = method.getAnnotation(FillToMethodResult.class);
        fillToMethodResult = (ann != null);

        MethodCheckExecType methodCheckExecType = method.getAnnotation(MethodCheckExecType.class);

        if (methodCheckExecType != null) {
            methodExecType = methodCheckExecType.execType();
        }
        Name nameAnn = method.getAnnotation(Name.class);
        if (nameAnn != null) {
            this.name = nameAnn.value();
        } else {
            this.name = method.toGenericString();
        }

    }

    @Override
    public MethodCheckResult check(CheckInstance checkInstance) {
        return (MethodCheckResult) super.check(checkInstance);
    }

    @Override
    public MethodCheckResult doCheck(CheckInstance checkInstance) {
        Object[] args = (Object[]) checkInstance.instance();

        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(
                methodExecType,
                ResultLevel.failedAndIgnoreNotBaseLogic);
        List<Supplier<CheckResult<?>>> execList = new ArrayList<>();

        for (MethodArgCheckConfig methodArgCheckConfig : methodArgCheckConfigList) {
            multiCheckerExecutor.add(() -> this.checkBaseLogicArgByConfig(methodArgCheckConfig, args[methodArgCheckConfig.index]));
        }

        String log = methodsToCheckersBean.getClass() + "|method:" + method.getName();
        MultiCheckResult<ArgCheckResult> multiCheckResult = (MultiCheckResult) multiCheckerExecutor.toMultiCheckResult(log);


        return new MethodCheckResult(
                multiCheckResult.mainCheckResult(),
                multiCheckResult.value(), methodsToCheckersBean, method
        );


    }


    private ArgCheckResult checkBaseLogicArgByConfig(MethodArgCheckConfig methodArgCheckConfig, Object checkArg) {

        CheckIt checkIt = methodArgCheckConfig.checkItAnn;


        MultiCheckerExecutor executor = new MultiCheckerExecutor(checkIt.execType(), checkIt.resultLevel());


//        List<Supplier<CheckResult<?>>> execList = new ArrayList<>();

        List<? extends ExecChecker<?, ?>> checkerList = methodArgCheckConfig.checkerNameList.stream().map(i -> purahContext.checkManager().get(i)).collect(Collectors.toList());

        for (Checker checker : checkerList) {
            executor.add(CheckInstance.create(checkArg, methodArgCheckConfig.clazz), checker);
//            execList.add(() -> checker.check(CheckInstance.create(checkArg, methodArgCheckConfig.clazz)));
        }

        ;
        String log = "method:" + method.getName() + "|arg" + methodArgCheckConfig.index;


        MultiCheckResult<CheckResult<?>> multiCheckResult = executor.toMultiCheckResult(log);


        return ArgCheckResult.create(multiCheckResult.mainCheckResult(), methodArgCheckConfig.checkerNameList,
                multiCheckResult.value(),
                checkIt, checkArg, methodExecType);


    }


    public Object fillObject(AutoFillCheckResult autoFillCheckResult) {

        if (this.isMethodCheckResultType()) {
            //获取函数检测结果
            return autoFillCheckResult.methodCheckResult();
        } else if (this.isArgCheckResultType()) {
            //获取第一个参数检测结果
            return autoFillCheckResult.argOf(0);
        } else if (this.isCombinatorialCheckResultType()) {
            //获取组合结果，内涵所有逻辑检测的结果
            return autoFillCheckResult.combinatorial();
        } else if (this.isBaseLogicResultType()) {
            //获取基础结果
            return autoFillCheckResult.main();
        } else if (this.isBooleanResultType()) {
            //获取boolean结果
            return autoFillCheckResult.isSuccess();
        } else if (this.isAutoFillCheckResultType()) {
            //默认自动填充
            return autoFillCheckResult;
        } else {
            throw new RuntimeException("asd");
        }
    }


    @Override
    public String logicFrom() {
        return this.method.getName();
    }

    @Override
    public Class<?> inputCheckInstanceClass() {
        return Object[].class;
    }

    @Override
    public Class<?> resultClass() {
        if (this.returnType instanceof Class) {
            return (Class<?>) this.returnType;

        }
        return super.resultClass();
    }

    private boolean isArgCheckResultType() {
        return ArgCheckResult.class.equals(returnType);
    }

    private boolean isAutoFillCheckResultType() {
        return AutoFillCheckResult.class.equals(returnType);
    }

    private boolean isMethodCheckResultType() {
        return MethodCheckResult.class.equals(returnType) || CheckResult.class.equals(returnType);
    }

    private boolean isBaseLogicResultType() {
        return BaseLogicCheckResult.class.equals(returnType);
    }

    private boolean isBooleanResultType() {
        return boolean.class.equals(returnType);
    }

    private boolean isCombinatorialCheckResultType() {
        return CombinatorialCheckResult.class.equals(returnType);
    }

    public boolean isFillToMethodResult() {
        return fillToMethodResult;
    }

    @Override
    public String name() {
        return name;
    }
}


//
//    @Override
//    public CheckResult check(CheckInstance checkInstance) {
//        CheckResult check = super.check(checkInstance);
//
//        CombinatorialCheckResult result = new CombinatorialCheckResult();
//        result.addResult(check);
//        result.setLogicFromByChecker(check.logicFrom());
//
//        return result;
//    }