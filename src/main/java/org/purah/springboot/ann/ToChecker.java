package org.purah.springboot.ann;


import org.purah.core.checker.method.toChecker.MethodToChecker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented

public @interface ToChecker {

    Class<? extends MethodToChecker> value() default MethodToChecker.class;
}
