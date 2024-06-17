package org.purah.springboot.result;

import org.purah.core.checker.result.*;

import java.util.List;

public class AutoFillCheckResult implements CheckResult<MethodCheckResult> {

    MethodCheckResult methodCheckResult;


    public AutoFillCheckResult(MethodCheckResult methodCheckResult) {
        this.methodCheckResult = methodCheckResult;
    }

    public BaseLogicCheckResult main() {
        return methodCheckResult.mainCheckResult();
    }

    public List<BaseLogicCheckResult> childList(ResultLevel resultLevel) {

        return methodCheckResult.resultChildList(resultLevel);
    }

    public List<BaseLogicCheckResult> childList() {

        return childList(ResultLevel.failedAndIgnoreNotBaseLogic);
    }


    public CombinatorialCheckResult combinatorial() {
        return CombinatorialCheckResult.create(methodCheckResult, ResultLevel.all);
    }


    public boolean successOf(int index) {
        return methodCheckResult.argResultOf(index).isSuccess();
    }

    public ArgCheckResult argOf(int index) {
        return methodCheckResult.argResultOf(index);
    }


    public MethodCheckResult methodCheckResult() {
        return value();
    }

    @Override
    public MethodCheckResult value() {
        return methodCheckResult;
    }

    @Override
    public Exception exception() {
        return methodCheckResult.exception();
    }

    @Override
    public ExecInfo execInfo() {
        return methodCheckResult.execInfo();
    }

    @Override
    public String log() {
        return methodCheckResult.log();
    }

    @Override
    public void setCheckLogicFrom(String logicFrom) {
        methodCheckResult.setCheckLogicFrom(logicFrom);
    }

    @Override
    public String checkLogicFrom() {
        return methodCheckResult.checkLogicFrom();
    }
}
