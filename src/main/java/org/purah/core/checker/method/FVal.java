package org.purah.core.checker.method;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface FVal {
    String value();

    String root = "#root#";
}
