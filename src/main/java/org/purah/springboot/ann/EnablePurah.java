package org.purah.springboot.ann;


import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.checker.method.toCheckerFactory.MethodToCheckerFactory;
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

    Class<? extends MethodToChecker> defaultMethodToCheckerClazz() default MethodToChecker.class;

    Class<? extends MethodToCheckerFactory> defaultMethodToCheckerFactoryClazz() default MethodToCheckerFactory.class;


    ResultLevel defaultResultLevel() default ResultLevel.failedAndIgnoreNotBaseLogic;

}
