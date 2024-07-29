package io.github.vajval.purah.spring.aop.ann;


import java.lang.annotation.*;

/*
 * MethodHandlerChecker 拦截函数生成  MethodCheckResult
 *
 *  @FillToMethodResult
    public CheckResult<?> customSyntax(@CheckIt("example:1[][*:custom_ann_check;*.*:custom_ann_check]") User user) {
        return null;
    }
    methodCheckResult to result  [CheckResult<?>]

    @FillToMethodResult
    public boolean customSyntax(@CheckIt("example:1[][*:custom_ann_check;*.*:custom_ann_check]") User user) {
        return null;
    }
    methodCheckResult.isSuccess() to result  [boolean]
 *
 */



@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface FillToMethodResult {


}
