package org.purah.springboot.ann;


import org.purah.springboot.core.ImportPurahRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(ImportPurahRegistrar.class)
public @interface EnableOnContext {

}
