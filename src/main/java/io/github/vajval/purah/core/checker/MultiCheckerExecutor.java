package io.github.vajval.purah.core.checker;


import io.github.vajval.purah.core.checker.combinatorial.CheckerExec;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个checker的执行器
 */

public class MultiCheckerExecutor {


    private final ExecMode.Main mainExecMode;
    private ExecInfo execInfo;
    private final ResultLevel resultLevel;

    private List<CheckResult<?>> finalExecResult;
    private final List<CheckerExec> checkSupplierExecList = new ArrayList<>();
    private final LogicCheckResult<Object> successMainResult;
    private final LogicCheckResult<Object> failedMainResult;
    private List<ExecInfo> execInfoList;


    public MultiCheckerExecutor(ExecMode.Main mainExecMode, ResultLevel resultLevel, String log) {
        this.mainExecMode = mainExecMode;
        this.resultLevel = resultLevel;
        if (mainExecMode == ExecMode.Main.all_success || mainExecMode == ExecMode.Main.all_success_but_must_check_all) {
            execInfo = ExecInfo.success;
        } else if (mainExecMode == ExecMode.Main.at_least_one || mainExecMode == ExecMode.Main.at_least_one_but_must_check_all) {
            execInfo = ExecInfo.failed;
        }
        successMainResult = LogicCheckResult.success();
        failedMainResult = LogicCheckResult.failed(null, ExecInfo.failed.value() + "  " + log );
    }

    public void add(CheckerExec checkerExec) {
        if (finalExecResult != null) {
            throw new RuntimeException("只能执行一次");
        }
        checkSupplierExecList.add(checkerExec);
    }


    public void add(Checker<?, ?> checker, InputToCheckerArg<?> inputToCheckerArg) {
        add(new CheckerExec(checker, inputToCheckerArg));
    }


    private void exec(List<CheckerExec> supplierList) {

        finalExecResult = new ArrayList<>();
        execInfoList=new ArrayList<>(supplierList.size());
        for (CheckerExec supplier : supplierList) {
            CheckResult<?> checkResult = supplier.exec();
            execInfoList.add(checkResult.execInfo());
            if (resultLevel.needBeCollected(checkResult)) {
                this.finalExecResult.add(checkResult);
            }
            if (checkResult.isIgnore()) {
                continue;
            }
            if (checkResult.isFailed()) {
                if (mainExecMode == ExecMode.Main.all_success) {   // 有错误 而要求必须要全部成功，才算成功
                    execInfo = ExecInfo.failed;
                    return;
                } else if (mainExecMode == ExecMode.Main.all_success_but_must_check_all) { // 有错误 而要求必须要全部成功，但是必须检查完
                    execInfo = ExecInfo.failed;
                }
            } else {
                if (mainExecMode == ExecMode.Main.at_least_one) {  // 没有错误 而且只要一个成功就够了
                    execInfo = ExecInfo.success;
                    return;
                } else if (mainExecMode == ExecMode.Main.at_least_one_but_must_check_all) {   // 没有错误  但是必须检查完
                    execInfo = ExecInfo.success;
                }
            }
        }
    }

    public List<ExecInfo> getExecInfoList() {
        if (finalExecResult == null) {
            throw new RuntimeException("还没执行呢");
        }
        return execInfoList;
    }

    public MultiCheckResult<CheckResult<?>> execToMultiCheckResult() {
        exec(checkSupplierExecList);
        if (execInfo.equals(ExecInfo.failed)) {
            return new MultiCheckResult<>(failedMainResult, finalExecResult);
        } else {
            return new MultiCheckResult<>(successMainResult, finalExecResult);
        }
    }


}