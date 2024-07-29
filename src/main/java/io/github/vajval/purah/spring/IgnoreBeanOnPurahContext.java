package io.github.vajval.purah.spring;


import java.lang.annotation.*;


/*
 * 放到bean的class上
 * PurahContext 会直接无视这个类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface IgnoreBeanOnPurahContext {

}
