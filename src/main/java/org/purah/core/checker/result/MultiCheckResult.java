package org.purah.core.checker.result;

import java.util.List;

public class MultiCheckResult<T extends CheckerResult> implements CheckerResult<List<T>> {


    protected  SingleCheckerResult mainCheckResult;

   protected List<T> valueList;

    public MultiCheckResult(SingleCheckerResult mainCheckResult, List<T> valueList) {
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

    public SingleCheckerResult mainCheckResult() {
        return mainCheckResult;
    }
}

