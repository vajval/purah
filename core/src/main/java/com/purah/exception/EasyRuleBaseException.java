package com.purah.exception;


/**
 * 规则异常
 * 所的规则在校验时抛出的异常最好都是继承于这个异常
 *
 */
public class EasyRuleBaseException extends RuntimeException {
    public EasyRuleBaseException(String message) {
        super(message);
    }

}
