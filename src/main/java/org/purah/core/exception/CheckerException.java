package org.purah.core.exception;


import org.purah.core.checker.base.Checker;

public class CheckerException extends PurahException {
    Checker checker;

    public CheckerException(Checker checker, String message) {
        super(message);
        this.checker = checker;

    }
}
