package org.purah.core.checker.result;


import java.util.ArrayList;
import java.util.List;

public class CombinatorialCheckResult extends MultiCheckResult<LogicCheckResult> {
    List<BaseOfMultiCheckResult> notBaseLogicResult;

    private CombinatorialCheckResult(BaseOfMultiCheckResult mainCheckResult, List<LogicCheckResult> valueList, List<BaseOfMultiCheckResult> notBaseLogicResult) {
        super(mainCheckResult, valueList);
        this.notBaseLogicResult = notBaseLogicResult;
    }

    public List<BaseOfMultiCheckResult> notBaseLogicResult() {
        return notBaseLogicResult;
    }


    public static CombinatorialCheckResult create(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<LogicCheckResult> logicCheckResultList = new ArrayList<>();
        List<BaseOfMultiCheckResult> notBaseLogicResult = new ArrayList<>();
        allBaseLogicCheckResultByRecursion(multiCheckResult, resultLevel, logicCheckResultList, notBaseLogicResult);

        return new CombinatorialCheckResult(multiCheckResult.base, logicCheckResultList, notBaseLogicResult);

    }


    @Override
    public String toString() {
        return "CombinatorialCheckResult{" +
                "mainCheckResult=" + base +
                ", value=" + valueList +
                '}';
    }


}


