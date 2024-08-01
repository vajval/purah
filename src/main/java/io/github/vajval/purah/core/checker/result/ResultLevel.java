package io.github.vajval.purah.core.checker.result;


import io.github.vajval.purah.core.exception.UnexpectedException;

public enum ResultLevel {
    //所有的结果,不论成功与否,是不是校验逻辑直接返回的结果
    //All results, regardless of success, and whether they are directly returned results of validation logic.
    all(1),
    //所有的结果,不论成功与否,只要校验逻辑直接返回的结果
    //All results, whether successful or not, as long as they are directly returned by the validation logic.
    all_only_base_logic(2),
    //只要失败的结果
    //Only the results of failures, .
    only_failed(3),
    //只要失败的结果,只要校验逻辑直接返回的结果
    //"Only the results of failures, as long as they are directly returned by the validation logic."
    only_failed_only_base_logic(4),
    //只要有异常的结果
    //Only results that have exceptions.
//    only_error(0)
    ;

    final int value;

    ResultLevel(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ResultLevel valueOf(int value) {
        if (value == 1) {
            return all;
        } else if (value == 2) {
            return all_only_base_logic;
        } else if (value == 3) {
            return only_failed;
        } else if (value == 4) {
            return only_failed_only_base_logic;
        }
//        else if (value == 0) {
//            return only_error;
//        }
        throw new UnexpectedException("ResultLevel value :" + value);
    }

    public boolean needBeCollected(CheckResult<?> checkResult) {
        if (checkResult.isIgnore()) {
            return false;
        }
        if (this == ResultLevel.all) {
            return true;
        } else if (this == ResultLevel.all_only_base_logic) {
            return true;
        } else if (this == ResultLevel.only_failed) {
            return !checkResult.isSuccess();
        } else if (this == ResultLevel.only_failed_only_base_logic) {
            return (!checkResult.isSuccess());
        }
//        else if (this == ResultLevel.only_error) {
//            return checkResult.isError();
//        }
        return false;
    }

    public boolean allowAddToFinalResult(CheckResult<?> checkResult) {

        if (checkResult.isIgnore()) {
            return false;
        }
        if (this == ResultLevel.all) {
            return true;
        } else if (this == ResultLevel.all_only_base_logic) {
            return checkResult instanceof LogicCheckResult;
        } else if (this == ResultLevel.only_failed) {
            return !checkResult.isSuccess();
        } else if (this == ResultLevel.only_failed_only_base_logic) {
            return (!checkResult.isSuccess()) && (checkResult instanceof LogicCheckResult);
        }
//        else if (this == ResultLevel.only_error) {
//            return checkResult.isError();
//        }
        return false;
    }
}
