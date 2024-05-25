package org.purah.core.exception;


import org.purah.core.checker.result.CheckerResult;

public class MethodArgCheckException extends PurahException {


    CheckerResult checkerResult;
    public MethodArgCheckException(CheckerResult checkerResult) {

        super(checkerResult.execInfo().name());
        this.checkerResult=checkerResult;

    }

    public CheckerResult checkerResult() {
        return checkerResult;
    }
}
