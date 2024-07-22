package org.purah.core.checker.result;

import java.util.ArrayList;
import java.util.List;

/*
{  //MultiCheckResult
  "main": "success",
  "valueList": [
    {   //MultiCheckResult
      "main": "success: 'id|name':'check1,check2'",
      "valueList": [
        {  //MultiCheckResult
          "main": "success: 'id':''check1,check2'",
          "valueList": [{"logic": "success:'id':check1"},{"logic": "success:'id':check2"}]//LogicCheckResult
        },
        {
          "main": "success: 'name':''check1,check2'",
          "valueList": [{"logic": "success:'name':check1"}, {"logic": "success:'name':check2"}]
        }
      ]
    }
  ]
}
 */


public class MultiCheckResult<T extends CheckResult<?>> implements CheckResult<List<T>> {

    protected final LogicCheckResult<?> mainResult;
    protected final List<T> valueList;

    public MultiCheckResult(LogicCheckResult<?> mainResult, List<T> valueList) {
        this.mainResult = mainResult;
        this.valueList = valueList;
    }


    public List<LogicCheckResult<?>> resultChildList(ResultLevel resultLevel) {

        List<LogicCheckResult<?>> resultList = new ArrayList<>();
        allBaseLogicCheckResultByRecursion(this, resultLevel, resultList);
        return resultList;
    }

    protected static void allBaseLogicCheckResultByRecursion(MultiCheckResult<?> multiCheckResult, ResultLevel resultLevel, List<LogicCheckResult<?>> logicCheckResultList) {

        if (resultLevel.allowAddToFinalResult(multiCheckResult)) {
            logicCheckResultList.add(multiCheckResult.mainResult);
        }
        if (multiCheckResult.valueList == null) return;

        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult<?> childResult = (MultiCheckResult) o;
                allBaseLogicCheckResultByRecursion(childResult, resultLevel, logicCheckResultList);
            } else if (o instanceof LogicCheckResult) {
                LogicCheckResult<?> logicCheckResult = (LogicCheckResult) o;

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
        return mainResult.exception();
    }

    @Override
    public ExecInfo execInfo() {
        return mainResult.execInfo();
    }

    @Override
    public String log() {
        return this.mainResult.log();
    }

    @Override
    public void setCheckLogicFrom(String logicFrom) {
        this.mainResult.setCheckLogicFrom(logicFrom);
    }

    @Override
    public String checkLogicFrom() {
        return this.mainResult.checkLogicFrom();
    }

    public LogicCheckResult<?> mainResult() {
        return mainResult;
    }

    @Override
    public String toString() {
        return "MultiCheckResult{" +
                "base=" + mainResult +
                ", valueList=" + valueList +
                '}';
    }
}

