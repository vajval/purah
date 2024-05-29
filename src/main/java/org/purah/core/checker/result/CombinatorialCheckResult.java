package org.purah.core.checker.result;



import java.util.List;

public class CombinatorialCheckResult extends MultiCheckResult<BaseLogicCheckResult> {

    private CombinatorialCheckResult(BaseLogicCheckResult mainCheckResult, List<BaseLogicCheckResult> valueList) {
        super(mainCheckResult, valueList);
    }


    public static CombinatorialCheckResult create(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<BaseLogicCheckResult> BaseLogicCheckResultList = allBaseLogicCheckResultByRecursion(multiCheckResult, resultLevel);


        return new CombinatorialCheckResult(multiCheckResult.mainCheckResult, BaseLogicCheckResultList);


    }


    @Override
    public String toString() {
        return "CombinatorialCheckResult{" +
                "mainCheckResult=" + mainCheckResult +
                ", value=" + valueList +
                '}';
    }


}


