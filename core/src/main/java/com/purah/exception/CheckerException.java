package com.purah.exception;

import com.purah.checker.Checker;

public class CheckerException extends PurahException {
    Checker checker;

    public CheckerException(Checker checker, String message) {
        super(message);
        this.checker = checker;

    }
}
