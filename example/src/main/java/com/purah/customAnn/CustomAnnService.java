package com.purah.customAnn;

import com.purah.checker.context.CheckerResult;
import com.purah.springboot.ann.CheckIt;

import com.purah.springboot.ann.FillToMethodResult;
import com.purah.customAnn.pojo.CustomUser;
import org.springframework.stereotype.Service;

@Service
public class CustomAnnService {

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
