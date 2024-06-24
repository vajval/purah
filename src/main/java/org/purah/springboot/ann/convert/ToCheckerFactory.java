package org.purah.springboot.ann.convert;

import org.purah.core.checker.factory.method.converter.MethodToCheckerFactoryConverter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ToCheckerFactory {

    String match();
    boolean cacheBeCreatedChecker() default true;

    Class<? extends MethodToCheckerFactoryConverter> value() default MethodToCheckerFactoryConverter.class;
}
