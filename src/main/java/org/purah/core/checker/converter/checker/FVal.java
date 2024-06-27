package org.purah.core.checker.converter.checker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface FVal {
    String value();

    String root = "#root#";
}
