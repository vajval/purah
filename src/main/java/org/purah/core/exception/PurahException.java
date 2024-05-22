package org.purah.core.exception;


/**
 * 规则异常
 * 所的规则在校验时抛出的异常最好都是继承于这个异常
 *
 */
public class PurahException extends RuntimeException {
    public PurahException(String message) {
        super(message);
    }

    public PurahException(Throwable cause) {
        super(cause);
    }
}
