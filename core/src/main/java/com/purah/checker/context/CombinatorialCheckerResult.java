package com.purah.checker.context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinatorialCheckerResult {

    List<SingleCheckerResult<?>> singleCheckerResultList = new ArrayList<>();
    public void addOtherRuleResult(CombinatorialCheckerResult ruleResult) {
        singleCheckerResultList.addAll(ruleResult.singleCheckerResultList);
    }

    public void addCheckerResult(SingleCheckerResult<?> singleCheckerResult) {
        singleCheckerResultList.add(singleCheckerResult);
    }

    public static CombinatorialCheckerResult create(SingleCheckerResult<?> singleCheckerResult) {
        CombinatorialCheckerResult result = new CombinatorialCheckerResult();
        result.addCheckerResult(singleCheckerResult);
        return result;
    }

    public boolean haveFailed() {
        return resultByExecType(ExecInfo.failed).size() == 0 ;

    }
    public boolean haveError() {
        return resultByExecType(ExecInfo.error).size() == 0;

    }



    private List<SingleCheckerResult<?>> resultByExecType(ExecInfo checkerExec) {
        return null;
//        return singleCheckerResultList.stream()
//                .filter(result -> result.resultType() == checkerExec)
//                .collect(Collectors.toList());
    }


    private <T> List<T> resultByExecType(Class<T> clazz, ExecInfo checkerExec) {
        return resultByExecType(checkerExec).stream().map(SingleCheckerResult::getData).map(i -> (T) i).collect(Collectors.toList());
    }

    public <T> List<T> successResult(Class<T> clazz) {
        return resultByExecType(clazz, ExecInfo.success);
    }

    public <T> List<T> failedResult(Class<T> clazz) {
        return resultByExecType(clazz, ExecInfo.failed);
    }

    public <T> List<T> errorResult(Class<T> clazz) {
        return resultByExecType(clazz, ExecInfo.error);
    }

}
