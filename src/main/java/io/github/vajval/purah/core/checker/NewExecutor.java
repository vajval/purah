package io.github.vajval.purah.core.checker;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NewExecutor {
    private ExecMode.Main mainExecMode;
    private ExecInfo execInfo;
    private final ResultLevel resultLevel;

    private List<CheckResult<?>> finalExecResult = new ArrayList<>();
    List<Checker<Object, Object>> checkerList;
    public List<ExecInfo> execInfoList = new ArrayList<>();
    public LogicCheckResult<Object> successMainResult;
    public LogicCheckResult<Object> failedMainResult;

    public NewExecutor(List<Checker<?, ?>> checkerList) {
        this(ExecMode.Main.all_success, ResultLevel.only_failed_only_base_logic, checkerList, "log");
    }

    public NewExecutor(ExecMode.Main mainExecMode, ResultLevel resultLevel, List<Checker<?, ?>> checkerList, String log) {
        this.mainExecMode = mainExecMode;
        this.resultLevel = resultLevel;
        if (mainExecMode == ExecMode.Main.all_success || mainExecMode == ExecMode.Main.all_success_but_must_check_all) {
            execInfo = ExecInfo.success;
        } else if (mainExecMode == ExecMode.Main.at_least_one || mainExecMode == ExecMode.Main.at_least_one_but_must_check_all) {
            execInfo = ExecInfo.failed;
        }
        this.checkerList = (List) checkerList;

        successMainResult = LogicCheckResult.success(null, ExecInfo.success.value() + " (" + log + ")");
        failedMainResult = LogicCheckResult.failed(null, ExecInfo.failed.value() + " (" + log + ")");

    }


    private void exec(Collection<InputToCheckerArg<?>> supplierList) {
        finalExecResult = new ArrayList<>(supplierList.size() * checkerList.size());
        for (Checker<Object, Object> checker : checkerList) {
            for (InputToCheckerArg<?> inputToCheckerArg : supplierList) {
                CheckResult<?> checkResult = checker.oCheck(inputToCheckerArg);
                if (resultLevel.needBeCollected(checkResult)) {
                    this.finalExecResult.add(checkResult);
                }
                if (!checkResult.isIgnore()) {
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
        }

    }


    public MultiCheckResult<CheckResult<?>> allToAll(Collection<InputToCheckerArg<?>> supplierList) {
        exec(supplierList);
        if (execInfo == ExecInfo.failed) {
            return new MultiCheckResult<>(failedMainResult, finalExecResult);
        } else {
            return new MultiCheckResult<>(successMainResult, finalExecResult);
        }
    }

}
