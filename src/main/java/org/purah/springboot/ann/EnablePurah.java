package org.purah.springboot.ann;


import org.purah.core.checker.method.converter.MethodToCheckerConverter;
import org.purah.core.checker.factory.method.converter.MethodToCheckerFactoryConverter;
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

    Class<? extends MethodToCheckerConverter> defaultMethodToCheckerClazz() default MethodToCheckerConverter.class;

    Class<? extends MethodToCheckerFactoryConverter> defaultMethodToCheckerFactoryClazz() default MethodToCheckerFactoryConverter.class;

    ResultLevel defaultResultLevel() default ResultLevel.failedAndIgnoreNotBaseLogic;

}
