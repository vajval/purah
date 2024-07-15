package org.purah.springboot.ioc.ann;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ToBaseMatcherFactory {
}
