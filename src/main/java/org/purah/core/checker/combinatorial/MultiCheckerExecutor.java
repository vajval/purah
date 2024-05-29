package org.purah.core.checker.combinatorial;

import org.purah.core.checker.result.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 多个checker的执行器
 */

public class MultiCheckerExecutor {


    ExecType.Main mainExecType;
    ExecInfo execInfo = ExecInfo.success;
    Exception e;

    List<CheckResult> fieldCheckResultList = new ArrayList<>();
    ResultLevel resultLevel;


    public MultiCheckerExecutor(ExecType.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;

    }

    public static boolean needAdd(CheckResult checkResult, ResultLevel resultLevel) {
        if (resultLevel == ResultLevel.all) {
            return true;
        } else if (resultLevel == ResultLevel.failed) {
            if (!checkResult.isSuccess()) {
                return true;
            }

        } else if (resultLevel == ResultLevel.failedAndIgnoreNotBaseLogic) {
            if ((!checkResult.isSuccess())) {
                return true;
            }

        } else if (resultLevel == ResultLevel.error) {
            if (checkResult.isError()) {
                return true;
            }
        }
        return false;
    }

    public ExecInfo exec(List<Supplier<CheckResult<?>>> ruleResultSupplierList) {
        ExecInfo result = ExecInfo.success;

        for (Supplier<? extends CheckResult<?>> supplier : ruleResultSupplierList) {
            CheckResult<?> checkResult = supplier.get();
            if (needAdd(checkResult, resultLevel)) {
                this.fieldCheckResultList.add(checkResult);
            }
            if (checkResult.isIgnore()) {
                continue;
            }
            /*
                   有错误直接返回
                 */
            if (checkResult.isError()) {
                execInfo = ExecInfo.error;
                e = checkResult.exception();
                return ExecInfo.error;
            }
            if (checkResult.isFailed()) {
                if (mainExecType == ExecType.Main.all_success) {
                    // 有错误 而要求必须要全部成功，才算成功
                    execInfo = ExecInfo.failed;
                    return execInfo;
                } else if (mainExecType == ExecType.Main.all_success_but_must_check_all) {
                    // 有错误 而要求必须要全部成功，但是必须检查完
                    execInfo = ExecInfo.failed;
                    result = execInfo;
                }
            } else {
                if (mainExecType == ExecType.Main.at_least_one) {
                    // 没有错误 而且只要一个成功就够了
                    execInfo = ExecInfo.success;
                    return execInfo;
                } else if (mainExecType == ExecType.Main.at_least_one_but_must_check_all) {
                    // 没有错误  但是必须检查完
                    execInfo = ExecInfo.success;
                    result = execInfo;
                }
            }
        }
        return result;


    }

    public MultiCheckResult<CheckResult<?>> multiCheckResult(String log) {


        BaseLogicCheckResult<Object> mainResult = null;
        if (execInfo.equals(ExecInfo.success)) {
            mainResult = BaseLogicCheckResult.success(null, execInfo.value() + " (" + log + ")");
        } else if (execInfo.equals(ExecInfo.failed)) {
            mainResult = BaseLogicCheckResult.failed(null, execInfo.value() + " (" + log + ")");

        } else if (execInfo.equals(ExecInfo.error)) {
            mainResult = BaseLogicCheckResult.error(e, execInfo.value() + " (" + log + ")");

        }
        return new MultiCheckResult(mainResult, fieldCheckResultList);


    }

    public CombinatorialCheckResult toCombinatorialCheckResult(String log) {
        MultiCheckResult<CheckResult<?>> multiCheckResult = multiCheckResult(log);
        return CombinatorialCheckResult.create(multiCheckResult, resultLevel);

    }
}