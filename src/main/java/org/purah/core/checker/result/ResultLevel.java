package org.purah.core.checker.result;

public enum ResultLevel {
    all(0),
    failed(1),
    failedAndIgnoreNotBaseLogic(2),
    error(3);

//    errorIgnoreMatch(4);

    int value;

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
}
