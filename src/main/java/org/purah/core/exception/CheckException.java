package org.purah.core.exception;


import org.purah.core.checker.Checker;

public class CheckException extends PurahException {
    final Checker<?,?> checker;

    public CheckException(Checker<?,?>checker, String message) {
        super(message);
        this.checker = checker;

    }
}
