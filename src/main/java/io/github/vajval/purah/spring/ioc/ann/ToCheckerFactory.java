package io.github.vajval.purah.spring.ioc.ann;

import java.lang.annotation.*;


/**
 * 只能用在有 @PurahMethodsRegBean注解的class的函数上
 * reg by PurahIocRegS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ToCheckerFactory {

    String match();
    boolean cacheBeCreatedChecker() default true;

}
