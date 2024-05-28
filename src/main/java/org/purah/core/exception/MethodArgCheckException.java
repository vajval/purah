package org.purah.core.exception;


import org.purah.core.checker.result.CheckResult;

public class MethodArgCheckException extends PurahException {


    CheckResult checkResult;
    public MethodArgCheckException(CheckResult checkResult) {

        super(checkResult.execInfo().name());
        this.checkResult = checkResult;

    }

    public CheckResult CheckResult() {
        return checkResult;
    }
}
