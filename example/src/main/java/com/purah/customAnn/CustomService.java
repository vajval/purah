package com.purah.customAnn;

import com.purah.checker.context.CheckerResult;
import com.purah.springboot.ann.CheckIt;

import com.purah.springboot.ann.FillToMethodResult;
import com.purah.customAnn.pojo.CustomUser;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
public class CustomService {


    /*
     * 自定义语法 详情见  CustomSyntaxChecker
     * 实现 所有字段自定义注解检测
     *
     * 相当于配置文件中
     * - name: "example:0[][i*:自定义注解检测]"
     *      exec_type: 0
     *      mapping:
     *         general:
     *           "[i*]": 自定义注解检测
     */
    @FillToMethodResult
    public boolean booleanCheckByCustomSyntax(@CheckIt("example:0[][i*:自定义注解检测]") CustomUser customUser) {
        return false;
    }

    /*
     * 自定义语法 详情见  CustomSyntaxChecker
     * 实现 所有字段及子对象自定义注解检测,及时出错也会检测所内容
     *
     * 相当于配置文件中
     * - name: "example:1[][*:自定义注解检测;*.*:自定义注解检测]"
     *      exec_type: 1
     *      mapping:
     *         general:
     *           "[*]": 自定义注解检测
     *           "[*.*]": 自定义注解检测
     * * 匹配
     * id
     * name
     * phone
     * age
     * childCustomUser
     * *.* 匹配
     * childCustomUser.id
     * childCustomUser.name
     * childCus
     * tomUser.phone
     * childCustomUser.age
     * childCustomUser.childCustomUser
     *
     */
    @FillToMethodResult
    public CheckerResult checkByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") CustomUser customUser) {
        return null;
    }

    /*
     * - name: 所有字段自定义注解检测
     *       mapping:
     *         custom_ann:
     *            "[*]": 自定义注解检测
     */

    @FillToMethodResult
    public boolean booleanCheckDefaultCheckerByClassAnn(@CheckIt CustomUser customUser) {
        return false;
    }


    public void voidCheck(@CheckIt("所有字段自定义注解检测") CustomUser customUser) {

    }

    @FillToMethodResult
    public CheckerResult checkResult(@CheckIt("所有字段自定义注解检测") CustomUser customUser) {
        return null;
    }

    @FillToMethodResult
    public boolean booleanCheck(@CheckIt("所有字段自定义注解检测") CustomUser customUser) {
        return false;
    }

    @FillToMethodResult
    public boolean booleanCheck(@CheckIt("所有字段自定义注解检测") CustomUser customUser0,

                                CustomUser customUser1,
                                @CheckIt("所有字段自定义注解检测") CustomUser customUser2) {
        return false;
    }

    @FillToMethodResult
    public boolean booleanCheck(@Validated CustomUser customUser0,

                                CustomUser customUser1
    ) {
        return false;
    }
}
