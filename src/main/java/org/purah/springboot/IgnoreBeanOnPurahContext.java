package org.purah.springboot;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface IgnoreBeanOnPurahContext {

}
