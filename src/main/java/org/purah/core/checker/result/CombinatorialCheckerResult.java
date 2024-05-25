package org.purah.core.checker.result;


import org.purah.core.checker.combinatorial.MultiCheckerExecutor;

import java.util.ArrayList;
import java.util.List;

public class CombinatorialCheckerResult extends MultiCheckResult<SingleCheckerResult> {

    private CombinatorialCheckerResult(SingleCheckerResult mainCheckResult, List<SingleCheckerResult> valueList) {
        super(mainCheckResult, valueList);
    }


    public static CombinatorialCheckerResult create(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<SingleCheckerResult> singleCheckerResultList = valueList(multiCheckResult, resultLevel);

        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult childResult = (MultiCheckResult) o;
                singleCheckerResultList.addAll(valueList(childResult, resultLevel));
            }
        }


        return new CombinatorialCheckerResult(multiCheckResult.mainCheckResult, singleCheckerResultList);


    }

    public static List<SingleCheckerResult> valueList(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<SingleCheckerResult> resultList = new ArrayList<>();

        boolean needAdd = MultiCheckerExecutor.needAdd(multiCheckResult, resultLevel);

        if (needAdd) {
            resultList.add(multiCheckResult.mainCheckResult());
        }

        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult childResult = (MultiCheckResult) o;
                resultList.addAll(valueList(childResult, resultLevel));
            }
        }


        return resultList;
    }











    @Override
    public String toString() {
        return "CombinatorialCheckerResult{" +
                "mainCheckResult=" + mainCheckResult +
                ", value=" + valueList +
                '}';
    }


}


