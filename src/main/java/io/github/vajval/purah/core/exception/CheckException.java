package io.github.vajval.purah.core.exception;


import io.github.vajval.purah.core.checker.Checker;

public class CheckException extends BasePurahException {
    final Checker<?,?> checker;

    public CheckException(Checker<?,?>checker, String message) {
        super(message);
        this.checker = checker;

    }
}
