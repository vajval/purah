package org.purah.core.checker.result;


import java.util.ArrayList;
import java.util.List;

public class CombinatorialCheckerResult implements CheckerResult<List<CheckerResult>> {

    boolean matchedResult;

    SingleCheckerResult mainCheckResult;

    List<CheckerResult> fieldCheckResultList;

    private CombinatorialCheckerResult(SingleCheckerResult mainCheckResult, List<CheckerResult> fieldCheckResultList, boolean matchedResult) {
        this.mainCheckResult = mainCheckResult;
        this.fieldCheckResultList = fieldCheckResultList;
        this.matchedResult = matchedResult;
    }

    public static class Builder {

        boolean matchedResult;
        List<CheckerResult> fieldCheckResultList = new ArrayList<>();

        private Builder(List<CheckerResult> beAddFieldCheckResultList, boolean matchedResult) {
            this.matchedResult = matchedResult;

            for (CheckerResult beAddCheckerResult : beAddFieldCheckResultList) {
                if (beAddCheckerResult instanceof CombinatorialCheckerResult) {
                    fieldCheckResultList.addAll(((CombinatorialCheckerResult) beAddCheckerResult).fieldCheckResultList);
                    fieldCheckResultList.add(((CombinatorialCheckerResult) beAddCheckerResult).getMainCheckResult());
                } else {
                    fieldCheckResultList.add(beAddCheckerResult);
                }

            }

        }

        public CombinatorialCheckerResult build(SingleCheckerResult mainCheckResult) {

            return new CombinatorialCheckerResult(mainCheckResult, fieldCheckResultList, matchedResult);

        }
    }

    public static Builder builder(List<CheckerResult> fieldCheckResultList, boolean matchedResult) {
        return new Builder(fieldCheckResultList, matchedResult);
    }

//    @Override
//    public boolean isMatchedResult() {
//        return matchedResult;
//    }

    public SingleCheckerResult getMainCheckResult() {
        return mainCheckResult;
    }


    public void setCheckLogicFrom(String logicFrom) {
        this.mainCheckResult.setCheckLogicFrom(logicFrom);


    }

    public String checkLogicFrom() {
        return this.mainCheckResult.checkLogicFrom();
    }


    public CombinatorialCheckerResult(List<CheckerResult> fieldCheckResultList) {
        this.fieldCheckResultList = fieldCheckResultList;
    }


    @Override
    public List<CheckerResult> value() {
        return fieldCheckResultList;
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

        return mainCheckResult.log();
    }


    @Override
    public String toString() {
        return "CombinatorialCheckerResult{" +
                "mainCheckResult=" + mainCheckResult +
                ", fieldCheckResultList=" + fieldCheckResultList +
                '}';
    }
}
