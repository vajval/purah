package org.purah.core.checker.result;

public enum ResultLevel {
    all(1),
    failed(2),
    failedAndIgnoreNotBaseLogic(3),
    failedNotBaseLogic(4),
    error(0);

//    errorIgnoreMatch(4);

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
//        else if (value == 4) {
//            return errorIgnoreMatch;
//        }
        throw new RuntimeException();
    }


    public boolean allowAddToFinalResult(CheckResult checkResult) {
        if (this == ResultLevel.all) {
            return true;
        } else if (this == ResultLevel.failed) {
            if (!checkResult.isSuccess()) {
                return true;
            }

        } else if (this == ResultLevel.failedAndIgnoreNotBaseLogic) {
            if ((!checkResult.isSuccess())) {
                return true;
            }

        } else if (this == ResultLevel.error) {
            if (checkResult.isError()) {
                return true;
            }
        } else if (this == ResultLevel.failedNotBaseLogic) {
            if (!checkResult.isSuccess()) {
                return true;
            }
        }
        return false;
    }
}
