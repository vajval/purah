package org.purah.core.exception;

public class FieldMatcherException  extends PurahException {
    public FieldMatcherException(String message) {
        super(message);
    }
    public FieldMatcherException(Exception e){
        super(e);
    }
}
