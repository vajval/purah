package io.github.vajval.purah.spring.ioc.ann;


import io.github.vajval.purah.core.checker.converter.checker.AutoNull;

import java.lang.annotation.*;

/**
 * 只能用在有 @PurahMethodsRegBean注解的class的函数上
 * reg by PurahIocRegS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface ToChecker {


    String value();

    AutoNull autoNull() default AutoNull.notEnable;

    String failedInfo() default "failed";

}
