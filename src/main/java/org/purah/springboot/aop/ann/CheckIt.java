package org.purah.springboot.aop.ann;

import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.ResultLevel;

import java.lang.annotation.*;


/*
  @CheckIt("user")
  class CustomUser{
  }
  class CustomPeople{
  }
 * public void voidCheck(@CheckIt("test") CustomUser customUser) {         //enable test
 * public void voidCheck(@CheckIt CustomUser customUser) {                 //enable user
 * public void voidCheck(@CheckIt("test") CustomPeople CustomPeople) {     //enable test
 * public void voidCheck(@CheckIt CustomPeople CustomPeople) {             //enable nothing
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Documented
public @interface CheckIt {
    /**
     * 使用的规则名字
     */

    String[] value() default {};

    ExecMode.Main mainMode() default ExecMode.Main.all_success;

    //todo
    ResultLevel resultLevel() default ResultLevel.all;


}
