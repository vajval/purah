package org.purah.springboot.ann.convert;


import org.purah.core.checker.converter.MethodConverter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented

public @interface ToChecker {


}
