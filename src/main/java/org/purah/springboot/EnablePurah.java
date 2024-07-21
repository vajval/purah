package org.purah.springboot;


import org.purah.core.checker.result.ResultLevel;
import org.purah.springboot.ioc.ImportPurahRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(ImportPurahRegistrar.class)
public @interface EnablePurah {

    boolean enableCache() default true;

    ResultLevel defaultResultLevel() default ResultLevel.only_failed_only_base_logic;

}
