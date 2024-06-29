package org.purah.core.checker.converter.checker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface FVal {
    //Fill in specified field values or annotations.
    String value();

    //root field
    String root = "#root#";
}
