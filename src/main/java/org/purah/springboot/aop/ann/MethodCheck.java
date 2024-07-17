package org.purah.springboot.aop.ann;

import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.ResultLevel;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodCheck {
    ExecMode.Main mainMode() default ExecMode.Main.all_success;

    ResultLevel resultLevel() default ResultLevel.all;

    boolean enableCache() default false;

}
