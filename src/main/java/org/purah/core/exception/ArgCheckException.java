package org.purah.core.exception;


import org.purah.core.checker.result.CheckerResult;

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
