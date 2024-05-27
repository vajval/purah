package org.purah.core.checker.result;


import org.purah.core.checker.combinatorial.MultiCheckerExecutor;

import java.util.ArrayList;
import java.util.List;

public class CombinatorialCheckResult extends MultiCheckResult<SingleCheckResult> {

    private CombinatorialCheckResult(SingleCheckResult mainCheckResult, List<SingleCheckResult> valueList) {
        super(mainCheckResult, valueList);
    }


    public static CombinatorialCheckResult create(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<SingleCheckResult> singleCheckerResultList = valueList(multiCheckResult, resultLevel);


        return new CombinatorialCheckResult(multiCheckResult.mainCheckResult, singleCheckerResultList);


    }

    public static List<SingleCheckResult> valueList(MultiCheckResult multiCheckResult, ResultLevel resultLevel) {

        List<SingleCheckResult> resultList = new ArrayList<>();


        for (Object o : multiCheckResult.valueList) {
            if (o instanceof MultiCheckResult) {
                MultiCheckResult childResult = (MultiCheckResult) o;
                resultList.addAll(valueList(childResult, resultLevel));
            } else if (o instanceof SingleCheckResult) {
                SingleCheckResult singleCheckerResult = (SingleCheckResult) o;
                boolean needAdd = MultiCheckerExecutor.needAdd(singleCheckerResult, resultLevel);
                if (needAdd) {
                    resultList.add(singleCheckerResult);
                }
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


