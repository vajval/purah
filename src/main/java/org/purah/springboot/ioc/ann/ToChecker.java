package org.purah.springboot.ioc.ann;


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

    // todo
    // not null auto check
    // String notNullCheck() default "not null"

}
