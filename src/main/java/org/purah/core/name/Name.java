package org.purah.core.name;



import java.lang.annotation.*;

/**
 *
 * 快捷实现 IName 接口的方法
 * IName quickly  Impl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface Name {
    String value();
}
