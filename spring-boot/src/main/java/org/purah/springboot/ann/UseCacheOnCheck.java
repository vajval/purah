package org.purah.springboot.ann;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented

public @interface UseCacheOnCheck {

//    Class<? extends RuleCaches> value() default BaseObjectRuleCaches.class;
}
