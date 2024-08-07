package io.github.vajval.purah.spring.aop;

import io.github.vajval.purah.core.checker.AbstractBaseSupportCacheChecker;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.MultiCheckerExecutor;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.spring.aop.ann.CheckIt;
import io.github.vajval.purah.spring.aop.result.ArgCheckResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ParameterHandlerChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {
    final CheckIt checkIt;
    final Purahs purahs;
    final List<String> checkerNameList;
    final Class<?> argClazz;
    final Method method;
    final int index;
    final Parameter parameter;

    public ParameterHandlerChecker(Purahs purahs, Parameter parameter, Method method, List<String> checkerNameList, int index) {
        this.parameter = parameter;
        this.method = method;
        this.index = index;
        this.purahs = purahs;
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
    public ArgCheckResult oCheck(Object o) {
        return (ArgCheckResult) super.oCheck(o);
    }

    @Override
    public ArgCheckResult check(InputToCheckerArg<Object> inputToCheckerArg) {
        return (ArgCheckResult) super.check(inputToCheckerArg);
    }

    @Override
    public ArgCheckResult doCheck(InputToCheckerArg<Object> checkArg) {


        MultiCheckerExecutor executor = new MultiCheckerExecutor(checkIt.mainMode(), checkIt.resultLevel());


        List<Checker<Object,Object>> checkerList = checkerNameList.stream().map(purahs::checkerOf).collect(Collectors.toList());

        for (Checker<?, ?> checker : checkerList) {
            executor.add( checker,checkArg);
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
