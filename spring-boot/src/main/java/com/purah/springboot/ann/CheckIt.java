package com.purah.springboot.ann;

import java.lang.annotation.*;


/**
 * 请放在参数上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface CheckIt {
    /**
     * 使用的规则名字
     */

    String[] value() default {};


}
