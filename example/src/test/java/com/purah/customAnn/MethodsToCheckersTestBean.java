package com.purah.customAnn;

import com.purah.base.Name;
import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.springboot.ann.MethodsToCheckers;
import org.springframework.util.StringUtils;

@MethodsToCheckers
@EnableOnPurahContext
public class MethodsToCheckersTestBean {

    @Name("非空判断FromTestBean")
    public boolean notEmpty(Object o) {
        return o != null;
    }

    @Name("有文本判断FromTestBean")
    public CheckerResult hasTest(String text) {
        if (StringUtils.hasText(text)) {
            return SingleCheckerResult.success(null, "有文本");
        } else {
            return SingleCheckerResult.failed(null, "无文本");
        }
    }

    @Name("数值判断FromTestBean")
    public CheckerResult range(CheckInstance<Number> checkInstance) {
        Integer value = checkInstance.instance().intValue();
        if (value < 0) {
            return SingleCheckerResult.failed(null, checkInstance.fieldStr() + ":取值错误:" + value);
        } else {
            return SingleCheckerResult.success(null, checkInstance.fieldStr() + ":取值正确:" + value);
        }
    }
}