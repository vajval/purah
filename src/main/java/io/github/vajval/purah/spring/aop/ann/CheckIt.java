package io.github.vajval.purah.spring.aop.ann;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.ResultLevel;

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




-----------------------------------
  class CustomPeople{
   @CheckIt("test")  //not enable
   String  name ;
  }

 * 可以通过继承 CustomAnnChecker ,实现注释掉的函数来支持,field上的注解检测

 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface CheckIt {
    /*
     * 使用的规则名字
     */

    String[] value() default {};

    ExecMode.Main mainMode() default ExecMode.Main.all_success;

    //todo
    ResultLevel resultLevel() default ResultLevel.all;


}
