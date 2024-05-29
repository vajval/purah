package org.purah.core.checker.result;

import java.util.ArrayList;
import java.util.List;

public class MultiCheckResult<T extends CheckResult> implements CheckResult<List<T>> {

    protected MainOfMultiCheckResult mainCheckResult;
    protected List<T> valueList;

    public MultiCheckResult(MainOfMultiCheckResult mainCheckResult, List<T> valueList) {
        this.mainCheckResult = mainCheckResult;
        this.valueList = valueList;
    }


    public List<BaseLogicCheckResult> allBaseLogicCheckResult(ResultLevel resultLevel) {
        List<BaseLogicCheckResult> resultList = new ArrayList<>();
        List<MainOfMultiCheckResult> multiCheckResultList = new ArrayList<>();
        allBaseLogicCheckResultByRecursion(this, resultLevel, resultList, multiCheckResultList);
        return resultList;
    }

    protected static void allBaseLogicCheckResultByRecursion(MultiCheckResult multiCheckResult, ResultLevel resultLevel, List<BaseLogicCheckResult> baseLogicCheckResultList, List<MainOfMultiCheckResult> multiCheckResultList) {

        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult childResult = (MultiCheckResult) o;

                if (resultLevel.needAddToFinalResult(childResult)) {
                    if (resultLevel == ResultLevel.failedAndIgnoreNotBaseLogic) {
                        multiCheckResultList.add(childResult.mainCheckResult);
                    }
                }
                allBaseLogicCheckResultByRecursion(childResult, resultLevel, baseLogicCheckResultList, multiCheckResultList);


            } else if (o instanceof BaseLogicCheckResult) {
                BaseLogicCheckResult baseLogicCheckResult = (BaseLogicCheckResult) o;
                boolean needAdd = resultLevel.needAddToFinalResult(baseLogicCheckResult);
                if (needAdd) {
                    baseLogicCheckResultList.add(baseLogicCheckResult);
                }
            }
        }
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

    public MainOfMultiCheckResult mainCheckResult() {
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

