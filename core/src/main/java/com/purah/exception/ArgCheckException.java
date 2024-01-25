package com.purah.exception;

import com.purah.checker.context.CheckerResult;

public class ArgCheckException  extends PurahException {


    CheckerResult checkerResult;
    public ArgCheckException( CheckerResult checkerResult) {

        super(checkerResult.execInfo().name());
        this.checkerResult=checkerResult;

    }

    public CheckerResult checkerResult() {
        return checkerResult;
    }
}
