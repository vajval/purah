package org.purah.core.checker.result;

import org.purah.core.checker.combinatorial.MultiCheckerExecutor;

import java.util.ArrayList;
import java.util.List;

public class MultiCheckResult<T extends CheckResult> implements CheckResult<List<T>> {

    protected BaseLogicCheckResult mainCheckResult;
    protected List<T> valueList;

    public MultiCheckResult(BaseLogicCheckResult mainCheckResult, List<T> valueList) {
        this.mainCheckResult = mainCheckResult;
        this.valueList = valueList;
    }


    public List<BaseLogicCheckResult> allBaseLogicCheckResult(ResultLevel resultLevel) {

        return allBaseLogicCheckResultByRecursion(this, resultLevel);
    }

    protected static List<BaseLogicCheckResult> allBaseLogicCheckResultByRecursion(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<BaseLogicCheckResult> resultList = new ArrayList<>();


        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult childResult = (MultiCheckResult) o;
                resultList.addAll(allBaseLogicCheckResultByRecursion(childResult, resultLevel));
            } else if (o instanceof BaseLogicCheckResult) {
                BaseLogicCheckResult baseLogicCheckResult = (BaseLogicCheckResult) o;
                boolean needAdd = resultLevel.needAddToFinalResult(baseLogicCheckResult);
                if (needAdd) {
                    resultList.add(baseLogicCheckResult);
                }
            }
        }
        return resultList;


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

    public BaseLogicCheckResult mainCheckResult() {
        return mainCheckResult;
    }

    @Override
    public String toString() {
        return "MultiCheckResult{" +
                "mainCheckResult=" + mainCheckResult +
                ", valueList=" + valueList +
                '}';
    }
}

