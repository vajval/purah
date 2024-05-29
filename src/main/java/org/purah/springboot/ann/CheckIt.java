package org.purah.springboot.ann;

import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.ResultLevel;

import java.lang.annotation.*;


/**
 * 使用方法 1
 * <p>
 * public void voidCheck(@CheckIt("所有字段自定义注解检测") CustomUser customUser) {
 * <p>
 * <p>
 * 使用方法 2
 *
 * @CheckIt("所有字段自定义注解检测") public class CustomUser {
 * A a;
 * B b;
 * }
 * public void voidCheck(@CheckIt CustomUser customUser) {
 * <p>
 * <p>
 * <p>
 * 两者的效果一样
 * <p>
 * 注意 如果
 * @CheckIt("AAA") public class CustomUser {
 * public void voidCheck(@CheckIt("BBBB") CustomUser customUser) {
 * 那么生效的只有BBBB
 * <p>
 * <p>
 * <p>
 * 只用参数注解checkIt注解内容为空时例如
 * @CheckIt("AAA") public class CustomUser {
 * public void voidCheck(@CheckIt CustomUser customUser) {
 * AAA 才会生效
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Documented
public @interface CheckIt {
    /**
     * 使用的规则名字
     */

    String[] value() default {};

    ExecType.Main execType() default ExecType.Main.all_success;

    ResultLevel resultLevel() default ResultLevel.failedAndIgnoreNotBaseLogic;




}
