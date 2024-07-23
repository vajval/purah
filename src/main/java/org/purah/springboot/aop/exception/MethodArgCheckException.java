package org.purah.springboot.aop.exception;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.BasePurahException;

public class MethodArgCheckException extends BasePurahException {


    final CheckResult<?> checkResult;

    public MethodArgCheckException(CheckResult<?> checkResult) {

        super(checkResult.execInfo().name());
        this.checkResult = checkResult;

    }

    public CheckResult<?> checkResult() {
        return checkResult;
    }
}
