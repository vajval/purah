package org.purah.springboot.ann;

import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.checker.method.toCheckerFactory.MethodToCheckerFactory;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ToCheckerFactory {
    String match();

    Class<? extends MethodToCheckerFactory> value() default MethodToCheckerFactory.class;
}
