package io.github.vajval.purah.core.checker.result;

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

    protected String info;

    public MultiCheckResult(LogicCheckResult<?> mainResult, List<T> valueList) {
        this.mainResult = mainResult;
        this.valueList = valueList;
        this.info = mainResult.info();
    }

    @Override
    public CheckResult<List<T>> updateInfo(String info) {
        this.info = info;
        return this;
    }

    @Override
    public String info() {
        return info;
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
    public List<T> value() {
        return valueList;
    }

    @Override
    public ExecInfo execInfo() {
        return mainResult.execInfo();
    }

    @Override
    public String log() {
        return this.mainResult.log();
    }

    public LogicCheckResult<?> mainResult() {
        return mainResult;
    }

    @Override
    public String toString() {
        return "MultiCheckResult{" +
                " mainResult=" + mainResult +
                ", valueList=" + valueList +
                '}';
    }
}

