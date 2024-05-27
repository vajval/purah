package org.purah.core.checker.result;

import java.util.List;

public class MultiCheckResult<T extends CheckResult> implements CheckResult<List<T>> {


    protected SingleCheckResult mainCheckResult;

   protected List<T> valueList;

    public MultiCheckResult(SingleCheckResult mainCheckResult, List<T> valueList) {
        this.mainCheckResult = mainCheckResult;
        this.valueList = valueList;
    }


    @Override
    public List<T> value() {
        return valueList;
    }

    @Override
    public Exception exception() {
        return mainCheckResult.exception();
    }

    @Override
    public ExecInfo execInfo() {
        return mainCheckResult.execInfo();
    }

    @Override
    public String log() {
        return this.mainCheckResult.log();
    }

    @Override
    public void setCheckLogicFrom(String logicFrom) {
        this.mainCheckResult.setCheckLogicFrom(logicFrom);


    }

    @Override
    public String checkLogicFrom() {
        return this.mainCheckResult.checkLogicFrom();
    }

    public SingleCheckResult mainCheckResult() {
        return mainCheckResult;
    }
}

