package org.purah.springboot.aop.ann;

import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.ResultLevel;

import java.lang.annotation.*;


/*
 * no  @MethodCheckConfig use default config
 * @MethodCheckConfig will override config on Method Aspect of @CheckIt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodCheckConfig {
    ExecMode.Main mainMode() default ExecMode.Main.all_success;

    ResultLevel resultLevel() default ResultLevel.all;

    boolean enableCache() default false;
    /*
     *
     *   PurahCheckInstanceCacheContext.execOnCacheContext(() -> methodExec());
     */

}
