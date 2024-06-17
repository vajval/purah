package org.purah.springboot.ann.convert;


import org.purah.core.checker.method.converter.MethodToCheckerConverter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented

public @interface ToChecker {


    Class<? extends MethodToCheckerConverter> value() default MethodToCheckerConverter.class;
}
