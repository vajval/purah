package org.purah.core.checker.result;

import java.util.ArrayList;
import java.util.List;

public class MultiCheckResult<T extends CheckResult> implements CheckResult<List<T>> {

    protected BaseOfMultiCheckResult base;
    protected List<T> valueList;

    public MultiCheckResult(BaseOfMultiCheckResult base, List<T> valueList) {
        this.base = base;
        this.valueList = valueList;
    }


    public List<LogicCheckResult> resultChildList(ResultLevel resultLevel) {

        List<LogicCheckResult> resultList = new ArrayList<>();
        List<BaseOfMultiCheckResult> multiCheckResultList = new ArrayList<>();
        allBaseLogicCheckResultByRecursion(this, resultLevel, resultList, multiCheckResultList);
        return resultList;
    }

    protected static void allBaseLogicCheckResultByRecursion(MultiCheckResult multiCheckResult, ResultLevel resultLevel, List<LogicCheckResult> logicCheckResultList, List<BaseOfMultiCheckResult> multiCheckResultList) {

        if (resultLevel.allowAddToFinalResult(multiCheckResult)) {
            if (resultLevel == ResultLevel.all) {
                multiCheckResultList.add(multiCheckResult.base);
            }
            if (resultLevel == ResultLevel.failedNotBaseLogic) {
                multiCheckResultList.add(multiCheckResult.base);
            }
        }
        if (multiCheckResult.valueList == null) return;

        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult childResult = (MultiCheckResult) o;
                allBaseLogicCheckResultByRecursion(childResult, resultLevel, logicCheckResultList, multiCheckResultList);
            } else if (o instanceof LogicCheckResult) {
                LogicCheckResult logicCheckResult = (LogicCheckResult) o;
                boolean allowAdd = resultLevel.allowAddToFinalResult(logicCheckResult);
                if (allowAdd) {
                    logicCheckResultList.add(logicCheckResult);
                }
            }
        }
    }


    @Override
    public List<T> data() {
        return valueList;
    }

    @Override
    public Exception exception() {
        return base.exception();
    }

    @Override
    public ExecInfo execInfo() {
        return base.execInfo();
    }

    @Override
    public String log() {
        return this.base.log();
    }

    @Override
    public void setCheckLogicFrom(String logicFrom) {
        this.base.setCheckLogicFrom(logicFrom);
    }

    @Override
    public String checkLogicFrom() {
        return this.base.checkLogicFrom();
    }

    public BaseOfMultiCheckResult mainCheckResult() {
        return base;
    }

    @Override
    public String toString() {
        return "MultiCheckResult{" +
                "base=" + base +
                ", valueList=" + valueList +
                '}';
    }
}

