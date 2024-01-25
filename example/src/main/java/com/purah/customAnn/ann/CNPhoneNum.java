package com.purah.customAnn.ann;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface CNPhoneNum {
    String errorMsg();
}


// private static final String REGEX_MOBILE = "^1[3|4|5|7|8][0-9]\\d{4,8}$";
//
//    public ChinaPhoneConstraint() {
//    }
//
//
//    @Override
//    protected boolean pass(IConstraintContext context, String value) {
//        return value.matches(REGEX_MOBILE);
//    }