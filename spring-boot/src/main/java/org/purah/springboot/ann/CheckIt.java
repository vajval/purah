package org.purah.springboot.ann;

import java.lang.annotation.*;


/**
 * 放在类上表示对所有方法生效
 * 放在方法上会 在执行方法时检查参数
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
