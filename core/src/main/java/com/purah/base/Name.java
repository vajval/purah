package com.purah.base;



import java.lang.annotation.*;

/**
 * 一些接口是必须要有名字,
 * 将这个注解放到类上面即可
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Name {
    String value();
}
