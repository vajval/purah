package com.purah.customAnn.ann;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Range {

    double min();
    double max();

    String errorMsg();
}
