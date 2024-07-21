package org.purah.springboot.aop.exception;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.PurahException;

public class MethodArgCheckException extends PurahException {


    final CheckResult checkResult;
    public MethodArgCheckException(CheckResult checkResult) {

        super(checkResult.execInfo().name());
        this.checkResult = checkResult;

    }

    public CheckResult CheckResult() {
        return checkResult;
    }
}
