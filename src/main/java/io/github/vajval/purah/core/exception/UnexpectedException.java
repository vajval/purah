package io.github.vajval.purah.core.exception;

public class UnexpectedException extends BasePurahException {
    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(Throwable cause) {
        super(cause);
    }

    public UnexpectedException() {
        super("un expected");
    }
}
