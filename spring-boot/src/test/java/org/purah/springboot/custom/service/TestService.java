package org.purah.springboot.custom.service;

import com.purah.checker.context.CheckerResult;
import org.purah.springboot.ann.CheckIt;

import org.purah.springboot.ann.FillToMethodResult;
import org.purah.springboot.custom.pojo.CustomUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class TestService {

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
