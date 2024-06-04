package org.purah.springboot.ann;

import org.purah.core.checker.factory.bymethod.MethodToCheckerFactory;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ToCheckerFactory {
    String match();

    boolean cacheBeCreatedChecker() default true;

    Class<? extends MethodToCheckerFactory> value() default MethodToCheckerFactory.class;
}
