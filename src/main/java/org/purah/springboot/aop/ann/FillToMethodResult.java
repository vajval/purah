package org.purah.springboot.aop.ann;


import java.lang.annotation.*;



@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface FillToMethodResult {


}
