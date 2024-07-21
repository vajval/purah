package org.purah.core.exception;

public class UnexpectedException  extends PurahException{
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
