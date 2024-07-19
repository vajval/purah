//package org.purah.example.customAnn;
//
//import org.purah.core.checker.result.CheckResult;
//import org.purah.core.checker.result.CombinatorialCheckResult;
//import org.purah.core.checker.result.LogicCheckResult;
//import org.purah.util.User;
//import org.purah.springboot.aop.ann.CheckIt;
//
//import org.purah.springboot.aop.ann.FillToMethodResult;
//import org.purah.springboot.aop.result.ArgCheckResult;
//import org.purah.springboot.aop.result.MethodCheckResult;
//import org.springframework.stereotype.Service;
//import org.springframework.validation.annotation.Validated;
//
//@Service
//public class CustomService {
//
//
//    /*
//     * 自定义语法 详情见  CustomSyntaxChecker
//     * 实现 所有字段自定义注解检测
//     *
//     * 相当于配置文件中
//     * - name: "example:0[][i*:自定义注解检测]"
//     *      exec_type: 0
//     *      mapping:
//     *         general:
//     *           "[i*]": 自定义注解检测
//     */
//    @FillToMethodResult
//    public boolean booleanCheckByCustomSyntax(@CheckIt("example:0[][i*:自定义注解检测]") User user) {
//        return false;
//    }
//
//    /*
//     * 自定义语法 详情见  CustomSyntaxChecker
//     * 实现 所有字段及子对象自定义注解检测,及时出错也会检测所内容
//     *
//     * 相当于配置文件中
//     * - name: "example:1[][*:自定义注解检测;*.*:自定义注解检测]"
//     *      exec_type: 1
//     *      mapping:
//     *         general:
//     *           "[*]": 自定义注解检测
//     *           "[*.*]": 自定义注解检测
//     * * 匹配
//     * id
//     * name
//     * phone
//     * age
//     * childCustomUser
//     * *.* 匹配
//     * childCustomUser.id
//     * childCustomUser.name
//     * childCus
//     * tomUser.phone
//     * childCustomUser.age
//     * childCustomUser.childCustomUser
//     *
//     */
//    @FillToMethodResult
//    public MethodCheckResult methodCheckByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") User user) {
//        return null;
//    }
//
//    @FillToMethodResult
//    public ArgCheckResult argCheckByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") User user) {
//        return null;
//    }
//
//    @FillToMethodResult
//    public CombinatorialCheckResult combinatorialCheckByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") User user) {
//        return null;
//    }
//
//    @FillToMethodResult
//    public LogicCheckResult BaseLogicCheckByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") User user) {
//        return null;
//    }
//
//

//
//
//
//    /*
//     * - name: 所有字段自定义注解检测
//     *       mapping:
//     *         custom_ann:
//     *            "[*]": 自定义注解检测
//     */
//
//    @FillToMethodResult
//    public boolean booleanCheckDefaultCheckerByClassAnn(@CheckIt User user) {
//        return false;
//    }
//
//
//    public void voidCheck(@CheckIt("所有字段自定义注解检测") User user) {
//
//    }
//
//    @FillToMethodResult
//    public CheckResult checkResult(@CheckIt("所有字段自定义注解检测") User user) {
//        return null;
//    }
//
//    @FillToMethodResult
//    public boolean booleanCheck(@CheckIt("所有字段自定义注解检测") User user) {
//        return false;
//    }
//
//    @FillToMethodResult
//    public boolean booleanCheck(@CheckIt("所有字段自定义注解检测") User user0,
//                                User user1,
//                                @CheckIt("所有字段自定义注解检测") User user2) {
//        return false;
//    }
//
//    @FillToMethodResult
//    public boolean booleanCheck(@Validated User user0,
//                               User user1
//    ) {
//        return false;
//    }
//}
