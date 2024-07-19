package org.purah.core.checker.result;


//todo
public enum ResultLevel {
    all(1),
    failed(2),
    failedAndIgnoreNotBaseLogic(3),
    failedNotBaseLogic(4),
    error(0);

    final int value;

    ResultLevel(int value) {
        this.value = value;
    }

    public int value() {
        return value;

    }

    public static ResultLevel valueOf(int value) {
        if (value == 0) {
            return all;
        } else if (value == 1) {
            return failed;
        } else if (value == 2) {
            return failedAndIgnoreNotBaseLogic;
        } else if (value == 3) {
            return error;
        }
        throw new RuntimeException();
    }


    public boolean allowAddToFinalResult(CheckResult<?> checkResult) {
        if (this == ResultLevel.all) {
            return true;
        } else if (this == ResultLevel.failed) {
            return !checkResult.isSuccess();

        } else if (this == ResultLevel.failedAndIgnoreNotBaseLogic) {
            return !checkResult.isSuccess();

        } else if (this == ResultLevel.error) {
            return checkResult.isError();
        } else if (this == ResultLevel.failedNotBaseLogic) {
            return !checkResult.isSuccess();
        }
        return false;
    }
}
