package org.purah.core.checker.result;


import java.util.ArrayList;
import java.util.List;

public class CombinatorialCheckResult extends MultiCheckResult<BaseLogicCheckResult> {
    List<MainOfMultiCheckResult> notBaseLogicResult;

    private CombinatorialCheckResult(MainOfMultiCheckResult mainCheckResult, List<BaseLogicCheckResult> valueList, List<MainOfMultiCheckResult> notBaseLogicResult) {
        super(mainCheckResult, valueList);
        this.notBaseLogicResult = notBaseLogicResult;
    }

    public List<MainOfMultiCheckResult> notBaseLogicResult() {
        return notBaseLogicResult;
    }


    public static CombinatorialCheckResult create(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<BaseLogicCheckResult> baseLogicCheckResultList = new ArrayList<>();
        List<MainOfMultiCheckResult> notBaseLogicResult = new ArrayList<>();
        allBaseLogicCheckResultByRecursion(multiCheckResult, resultLevel, baseLogicCheckResultList, notBaseLogicResult);

        return new CombinatorialCheckResult(multiCheckResult.mainCheckResult, baseLogicCheckResultList, notBaseLogicResult);

    }


    @Override
    public String toString() {
        return "CombinatorialCheckResult{" +
                "mainCheckResult=" + mainCheckResult +
                ", value=" + valueList +
                '}';
    }


}


