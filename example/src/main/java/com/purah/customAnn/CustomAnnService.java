package com.purah.customAnn;

import com.purah.checker.context.CheckerResult;
import com.purah.springboot.ann.CheckIt;

import com.purah.springboot.ann.FillToMethodResult;
import com.purah.customAnn.pojo.CustomUser;
import org.springframework.stereotype.Service;

@Service
public class CustomAnnService {


    /**
     *
     *
     * 自定义语法 详情见  CustomSyntaxChecker
     * - name: 所有字段自定义注解检测
     *      mapping:
     *        general:
     *           "[i*]": 自定义注解检测
     *
     */
    @FillToMethodResult
    public boolean booleanCheckByCustomSyntax(@CheckIt("example:0[][i*:自定义注解检测]") CustomUser customUser) {
        return false;
    }
    @FillToMethodResult
    public CheckerResult checkByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") CustomUser customUser) {
        return null;
    }

    /**
     * - name: 所有字段自定义注解检测
     *       mapping:
     *         custom_ann:
     *            "[*]": 自定义注解检测
     *
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
}
