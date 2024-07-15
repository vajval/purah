package org.purah.springboot.ioc.ann;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ToCheckerFactory {

    String match();
    boolean cacheBeCreatedChecker() default true;

}
