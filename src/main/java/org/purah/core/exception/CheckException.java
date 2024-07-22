package org.purah.core.exception;


import org.purah.core.checker.Checker;

public class CheckException extends BasePurahException {
    final Checker<?,?> checker;

    public CheckException(Checker<?,?>checker, String message) {
        super(message);
        this.checker = checker;

    }
}
