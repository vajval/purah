package org.purah.springboot.ann;


import org.purah.core.base.NameUtil;
import org.purah.core.checker.method.toChecker.MethodToChecker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented

public @interface ToChecker {

    String name() default "";

    Class<? extends MethodToChecker> value() default MethodToChecker.class;
}
