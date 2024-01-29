package com.purah.springboot.aop;

import com.google.common.collect.Lists;
import com.purah.PurahContext;
import com.purah.base.Name;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.ExecChecker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.checker.method.MethodToChecker;
import com.purah.springboot.ann.CheckIt;
import com.purah.springboot.ann.FillToMethodResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodHandlerChecker extends MethodToChecker {

    protected PurahContext purahContext;
    protected List<MethodArgCheckConfig> methodArgCheckConfigList;

    protected boolean fillToMethodResult;

    public MethodHandlerChecker(Object methodsToCheckersBean, Method method, PurahContext purahContext) {
        super(methodsToCheckersBean, method);
        this.purahContext = purahContext;
    }

    @Override
    protected void init() {
        super.init();
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

            List<String> useCheckerNameList = Stream.of(checkIt.value()).toList();
            Class<?> argClazz = parameter.getType();
            if (useCheckerNameList.size() == 0) {

                CheckIt argClazzCheckIt = argClazz.getDeclaredAnnotation(CheckIt.class);
                if (argClazzCheckIt == null) continue;
                useCheckerNameList = Stream.of(argClazzCheckIt.value()).toList();

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

    public static boolean support(Object methodsToCheckersBean, Method method) {
        String errorMsg = staticErrorMsg(methodsToCheckersBean, method);
        return errorMsg == null;
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


    @Override
    protected String errorMsg(Object methodsToCheckersBean, Method method) {


        return staticErrorMsg(methodsToCheckersBean, method);
    }

    @Override
    public CombinatorialCheckerResult check(CheckInstance checkInstance) {
        return (CombinatorialCheckerResult) super.check(checkInstance);
    }

    @Override
    public CombinatorialCheckerResult doCheck(CheckInstance checkInstance) {
        Object[] args = (Object[]) checkInstance.instance();
        CombinatorialCheckerResult result = new CombinatorialCheckerResult();
        for (MethodArgCheckConfig methodArgCheckConfig : methodArgCheckConfigList) {


            List<CheckerResult> childRusultList = this.checkArgByConfig(methodArgCheckConfig, args[methodArgCheckConfig.index]);
            for (CheckerResult childResult : childRusultList) {
                result.addResult(childResult);
            }
            if (result.isFailed()) {
                return result;
            }
        }
        return result;
    }


    private List<CheckerResult> checkArgByConfig(MethodArgCheckConfig methodArgCheckConfig, Object arg) {
        List<CheckerResult> resultList = new ArrayList<>();

        List<? extends ExecChecker<?, ?>> checkerList = methodArgCheckConfig.checkerNameList.stream().map(i -> purahContext.checkManager().get(i)).toList();
        for (Checker checker : checkerList) {
            CheckerResult ruleResult = checker.check(CheckInstance.create(arg));
            resultList.add(ruleResult);
            if (ruleResult.isFailed()) return resultList;

        }
        return resultList;
    }

}
