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

    private List<CheckResult<?>> finalExecResult = new ArrayList<>();
    private final List<CheckerExec> checkSupplierExecList = new ArrayList<>();


    public MultiCheckerExecutor(ExecMode.Main mainExecMode, ResultLevel resultLevel) {
        this.mainExecMode = mainExecMode;
        this.resultLevel = resultLevel;
        if (mainExecMode == ExecMode.Main.all_success || mainExecMode == ExecMode.Main.all_success_but_must_check_all) {
            execInfo = ExecInfo.success;
        } else if (mainExecMode == ExecMode.Main.at_least_one || mainExecMode == ExecMode.Main.at_least_one_but_must_check_all) {
            execInfo = ExecInfo.failed;
        }
    }


    public void add(CheckerExec checkerExec) {
        checkSupplierExecList.add(checkerExec);
    }


    public void add(Checker<?, ?> checker, InputToCheckerArg<?> inputToCheckerArg) {
        add(new CheckerExec(checker, inputToCheckerArg));
    }

    public List<ExecInfo> getExecInfoList() {
        return execInfoList;
    }

    public List<ExecInfo> execInfoList = new ArrayList<>();

    private void exec(List<CheckerExec> supplierList) {
        finalExecResult = new ArrayList<>(checkSupplierExecList.size());
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


    public MultiCheckResult<CheckResult<?>> toMultiCheckResult(String log) {
        exec(checkSupplierExecList);
        LogicCheckResult<Object> mainResult = null;
        if (execInfo.equals(ExecInfo.success)) {
            mainResult = LogicCheckResult.success(null, execInfo.value() + " (" + log + ")");
        } else if (execInfo.equals(ExecInfo.failed)) {
            mainResult = LogicCheckResult.failed(null, execInfo.value() + " (" + log + ")");
        }
        return new MultiCheckResult<>(mainResult, finalExecResult);


    }


}