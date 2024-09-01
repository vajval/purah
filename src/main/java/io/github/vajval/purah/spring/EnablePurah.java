package io.github.vajval.purah.spring;


import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.spring.ioc.ImportPurahRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/*

 * 放到启动类上以使之生效
 * @SpringBootApplication
 * @EnablePurah
 * public class ExampleApplication {
 *
 *     public static void main(String[] args) {
 *
 *         SpringApplication.run(ExampleApplication.class, args);
 *     }
 * }
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(ImportPurahRegistrar.class)
public @interface EnablePurah {

    boolean checkItAspect() default true;

    boolean enableCache() default false;

    boolean enableExtendUnsafeCache() default false;



}
