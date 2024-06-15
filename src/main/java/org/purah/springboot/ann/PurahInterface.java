package org.purah.springboot.ann;

import org.purah.springboot.ioc.ImportPurahRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface PurahInterface {

}
