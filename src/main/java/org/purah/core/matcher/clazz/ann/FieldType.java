package org.purah.core.matcher.clazz.ann;

import java.lang.annotation.*;


/**
 * 自带的一个，见单元测试
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface FieldType {
    String[] value();
}
