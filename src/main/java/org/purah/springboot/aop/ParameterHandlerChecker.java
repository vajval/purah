package org.purah.springboot.aop;

import org.purah.core.PurahContext;
import org.purah.core.checker.*;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.LogicCheckResult;
import org.purah.core.checker.result.MultiCheckResult;
import org.purah.springboot.aop.ann.CheckIt;
import org.purah.springboot.aop.result.ArgCheckResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ParameterHandlerChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {
    final CheckIt checkIt;
    final PurahContext purahContext;
    final List<String> checkerNameList;
    final Class<?> argClazz;
    final Method method;
    final int index;
    final Parameter parameter;

    public ParameterHandlerChecker(PurahContext purahContext, Parameter parameter, Method method, List<String> checkerNameList, int index) {
        this.parameter = parameter;
        this.method = method;
        this.index = index;
        this.purahContext = purahContext;
        this.checkIt = parameter.getAnnotation(CheckIt.class);
        this.checkerNameList = checkerNameList;
        this.argClazz = parameter.getType();
    }

    @Override
    public String name() {
        return "arg " + index + " of " + method.toGenericString();
    }

    public Class<?> argClazz() {
        return argClazz;
    }
    @Override
    public boolean enableCache() {
        return true;
    }
    @Override
    public ArgCheckResult check(Object o) {
        return (ArgCheckResult) super.check(o);
    }

    @Override
    public ArgCheckResult check(InputToCheckerArg<Object> inputToCheckerArg) {
        return (ArgCheckResult) super.check(inputToCheckerArg);
    }

    @Override
    public ArgCheckResult doCheck(InputToCheckerArg<Object> checkArg) {


        MultiCheckerExecutor executor = new MultiCheckerExecutor(checkIt.mainMode(), checkIt.resultLevel());


        List<GenericsProxyChecker> checkerList = checkerNameList.stream().map(i -> purahContext.checkManager().of(i)).collect(Collectors.toList());

        for (Checker<?, ?> checker : checkerList) {
            executor.add(checkArg, checker);
        }

        String log = "arg" + index + "  of method:" + method.toGenericString();


        MultiCheckResult<CheckResult<?>> multiCheckResult = executor.toMultiCheckResult(log);


        return ArgCheckResult.create(multiCheckResult.mainResult(), checkerNameList,
                multiCheckResult.data(),
                checkIt, checkArg, checkIt.mainMode());

    }

    protected ArgCheckResult createIgnoreResult(Object arg, ExecMode.Main execMode) {
        String log = "this arg not check,no ann or be skip";

        if (execMode == ExecMode.Main.at_least_one) {
            log = "unable to know,mode:[at_least_one],at least one has already succeeded.So this check is skipped,If the check must be conducted regardless. @MethodCheck set mainMode  at_least_one_but_must_check_all";
        } else if (execMode == ExecMode.Main.all_success) {
            log = "unable to know,mode:[all_success],at least one has already failed.So this check is skipped,If the check must be conducted regardless. @MethodCheck set mainMode  all_success_but_must_check_all";
        }
        InputToCheckerArg<Object> checkArg = InputToCheckerArg.of(arg, argClazz());
        return ArgCheckResult.create(LogicCheckResult.ignore(log), checkerNameList,
                Collections.emptyList(),
                checkIt, checkArg, checkIt.mainMode());
    }


}
