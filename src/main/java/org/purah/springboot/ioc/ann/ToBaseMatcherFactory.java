package org.purah.springboot.ioc.ann;

import java.lang.annotation.*;


/**
 * 这个类实现了FieldMatcher接口,且有一个string类型的单参构造器
 * 不需要@Component 注解,会扫描这个类生成MatcherFactory bean 注册到 purahContext中
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ToBaseMatcherFactory {
}
