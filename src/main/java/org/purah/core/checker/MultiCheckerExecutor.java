package org.purah.core.checker;


import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 多个checker的执行器
 */

public class MultiCheckerExecutor {


    private final ExecMode.Main mainExecType;
    private ExecInfo execInfo = ExecInfo.success;
    private final ResultLevel resultLevel;
    private Exception e;
    private boolean exec = false;

    private final List<CheckResult<?>> finalExecResult = new ArrayList<>();


    private final List<Supplier<CheckResult<?>>> ruleResultSupplierList = new ArrayList<>();


    public MultiCheckerExecutor(ExecMode.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;

    }


    public void add(Supplier<CheckResult<?>> supplier) {
        if (exec) {
            throw new RuntimeException("已经执行完了，不能在添加了");
        }
        ruleResultSupplierList.add(supplier);

    }


    public void add(InputToCheckerArg<?> inputToCheckerArg, Checker<?, ?> checker) {
        if (exec) {
            throw new RuntimeException("已经执行完了，不能在添加了");
        }
        Supplier<CheckResult<?>> supplier = () -> ((Checker) checker).check(inputToCheckerArg);

        this.add((supplier));
    }


    private void execIfNotExec(List<Supplier<CheckResult<?>>> ruleResultSupplierList) {
        if (exec) return;
        exec = true;
        ExecInfo result = ExecInfo.success;


        for (Supplier<? extends CheckResult<?>> supplier : ruleResultSupplierList) {
            CheckResult<?> checkResult = supplier.get();


            if (resultLevel.allowAddToFinalResult(checkResult)) {
                this.finalExecResult.add(checkResult);
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
                return;
            }
            if (checkResult.isFailed()) {
                if (mainExecType == ExecMode.Main.all_success) {
                    // 有错误 而要求必须要全部成功，才算成功
                    execInfo = ExecInfo.failed;
                    return;
                } else if (mainExecType == ExecMode.Main.all_success_but_must_check_all) {
                    // 有错误 而要求必须要全部成功，但是必须检查完
                    execInfo = ExecInfo.failed;
                    result = execInfo;
                }
            } else {
                if (mainExecType == ExecMode.Main.at_least_one) {
                    // 没有错误 而且只要一个成功就够了
                    execInfo = ExecInfo.success;
                    return;
                } else if (mainExecType == ExecMode.Main.at_least_one_but_must_check_all) {
                    // 没有错误  但是必须检查完
                    execInfo = ExecInfo.success;
                    result = execInfo;
                }
            }
        }


    }

    /**
     *
     */

    public MultiCheckResult<CheckResult<?>> toMultiCheckResult(String log) {
        execIfNotExec(ruleResultSupplierList);
        return multiCheckResult(log);
    }



    private MultiCheckResult<CheckResult<?>> multiCheckResult(String log) {

        LogicCheckResult<Object> mainResult = null;
        if (execInfo.equals(ExecInfo.success)) {
            mainResult = LogicCheckResult.success(null, execInfo.value() + " (" + log + ")");
        } else if (execInfo.equals(ExecInfo.failed)) {
            mainResult = LogicCheckResult.failed(null, execInfo.value() + " (" + log + ")");
        } else if (execInfo.equals(ExecInfo.error)) {
            mainResult = LogicCheckResult.error(e, execInfo.value() + " (" + log + ")");
        }
        return new MultiCheckResult<>(mainResult, finalExecResult);


    }


}