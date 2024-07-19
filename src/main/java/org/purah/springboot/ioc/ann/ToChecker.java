package org.purah.springboot.ioc.ann;



import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented

public @interface ToChecker {


}
