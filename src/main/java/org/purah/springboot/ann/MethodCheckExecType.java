package org.purah.springboot.ann;

import org.purah.core.checker.combinatorial.ExecType;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodCheckExecType {
    ExecType.Main execType() default ExecType.Main.all_success;

}
