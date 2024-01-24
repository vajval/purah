package com.purah.checker.context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinatorialCheckerResult implements CheckerResult<List<CheckerResult>>{

    List<CheckerResult> singleCheckerResultList = new ArrayList<>();
    ExecInfo execInfo=ExecInfo.success;
    Exception e;

    public void setExecInfo(ExecInfo execInfo) {
        this.execInfo = execInfo;
    }

    public CombinatorialCheckerResult() {


    }


    public CombinatorialCheckerResult(Exception e) {
        this.e = e;
    }

    public void  addResult(CheckerResult checkerResult){
        if(checkerResult instanceof  CombinatorialCheckerResult){
            singleCheckerResultList.addAll  (((CombinatorialCheckerResult)checkerResult).singleCheckerResultList);
        }else{
            singleCheckerResultList.add(checkerResult);
        }
        if(checkerResult.isFailed()){
            setExecInfo(ExecInfo.failed);
        }
        if(checkerResult.isError()){
            setExecInfo(ExecInfo.error);
        }
    }

    public CombinatorialCheckerResult(List<CheckerResult> singleCheckerResultList) {
        this.singleCheckerResultList = singleCheckerResultList;
    }



    @Override
    public List<CheckerResult> value() {
        return singleCheckerResultList;
    }

    @Override
    public Exception exception() {
        return e;
    }

    @Override
    public ExecInfo execInfo() {
        return execInfo;
    }

    @Override
    public String toString() {
        return "CombinatorialCheckerResult{" +
                "singleCheckerResultList=" + singleCheckerResultList +
                ", execInfo=" + execInfo +
                ", e=" + e +
                '}';
    }
    //    List<SingleCheckerResult<?>> singleCheckerResultList = new ArrayList<>();
//    public void addOtherRuleResult(CombinatorialCheckerResult ruleResult) {
//        singleCheckerResultList.addAll(ruleResult.singleCheckerResultList);
//    }
//
//    public void addCheckerResult(SingleCheckerResult<?> singleCheckerResult) {
//        singleCheckerResultList.add(singleCheckerResult);
//    }
//
//    public static CombinatorialCheckerResult create(SingleCheckerResult<?> singleCheckerResult) {
//        CombinatorialCheckerResult result = new CombinatorialCheckerResult();
//        result.addCheckerResult(singleCheckerResult);
//        return result;
//    }
//
//    public boolean haveFailed() {
//        return resultByExecType(ExecInfo.failed).size() == 0 ;
//
//    }
//    public boolean haveError() {
//        return resultByExecType(ExecInfo.error).size() == 0;
//
//    }
//
//
//
//    private List<SingleCheckerResult<?>> resultByExecType(ExecInfo checkerExec) {
//        return null;
////        return singleCheckerResultList.stream()
////                .filter(result -> result.resultType() == checkerExec)
////                .collect(Collectors.toList());
//    }
//
//
//    private <T> List<T> resultByExecType(Class<T> clazz, ExecInfo checkerExec) {
//        return resultByExecType(checkerExec).stream().map(SingleCheckerResult::getData).map(i -> (T) i).collect(Collectors.toList());
//    }
//
//    public <T> List<T> successResult(Class<T> clazz) {
//        return resultByExecType(clazz, ExecInfo.success);
//    }
//
//    public <T> List<T> failedResult(Class<T> clazz) {
//        return resultByExecType(clazz, ExecInfo.failed);
//    }
//
//    public <T> List<T> errorResult(Class<T> clazz) {
//        return resultByExecType(clazz, ExecInfo.error);
//    }

}
