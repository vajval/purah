package org.purah.springboot.ioc.ann;

import java.lang.annotation.*;


/**
 * 只能用在有 @PurahMethodsRegBean注解的class的函数上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ToCheckerFactory {

    String match();
    boolean cacheBeCreatedChecker() default true;

}
