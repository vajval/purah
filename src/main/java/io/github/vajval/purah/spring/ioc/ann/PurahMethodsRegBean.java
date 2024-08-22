package io.github.vajval.purah.spring.ioc.ann;

import java.lang.annotation.*;

/**
 * 将bean中有@ToChecker的method 转换成 checker 注册到 purahContext
 * 将bean中有@ToCheckerFactory的method 转换成 checkerFactory 注册到 purahContext
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented

public @interface PurahMethodsRegBean {
}
