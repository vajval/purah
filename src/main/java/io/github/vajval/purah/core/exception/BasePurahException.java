package io.github.vajval.purah.core.exception;


/**
 * Base Exception
 *
 */
public class BasePurahException extends RuntimeException {
    public BasePurahException(String message) {
        super(message);
    }

    public BasePurahException(Throwable cause) {
        super(cause);
    }
}
