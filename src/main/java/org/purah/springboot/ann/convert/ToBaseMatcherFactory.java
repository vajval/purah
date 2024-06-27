package org.purah.springboot.ann.convert;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ToBaseMatcherFactory {
}
