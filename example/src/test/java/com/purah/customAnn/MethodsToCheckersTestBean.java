package com.purah.customAnn;

import com.purah.base.Name;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.springboot.ann.PurahEnableMethods;
import com.purah.springboot.ann.ToChecker;
import com.purah.springboot.ann.ToCheckerFactory;
import org.springframework.util.StringUtils;

@PurahEnableMethods
@EnableOnPurahContext
public class MethodsToCheckersTestBean {

    @Name("非空判断FromTestBean")
    @ToChecker
    public boolean notEmpty(Object o) {
        return o != null;
    }

    @Name("有文本判断FromTestBean")
    @ToChecker
    public CheckerResult hasTest(String text) {
        if (StringUtils.hasText(text)) {
            return SingleCheckerResult.success(null, "有文本");
        } else {
            return SingleCheckerResult.failed(null, "无文本");
        }
    }

    @ToCheckerFactory(match = "1取值必须在[*-*]之间判断FromTestBean")
    public boolean range(String name, Number value) {

        String[] split = name.substring(name.indexOf("["), name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }

    @ToCheckerFactory(match = "2取值必须在[*-*]之间判断FromTestBean")
    public CheckerResult range2(String name, Number value) {

        String[] split = name.substring(name.indexOf("["), name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        boolean success = value.doubleValue() >= min && value.doubleValue() <= max;

        if (success) {
            return SingleCheckerResult.success();
        }
        return SingleCheckerResult.failed(null, "failed");
    }

    @ToCheckerFactory(match = "3取值必须在[*-*]之间判断FromTestBean")
    public Checker range3(String name) {

        return new BaseChecker<Number, Object>() {
            @Override
            public CheckerResult doCheck(CheckInstance<Number> checkInstance) {
                return range2(name, checkInstance.instance());
            }
        };
    }

    @Name("数值判断FromTestBean")
    @ToChecker
    public CheckerResult range(CheckInstance<Number> checkInstance) {
        Integer value = checkInstance.instance().intValue();
        if (value < 0) {
            return SingleCheckerResult.failed(null, checkInstance.fieldStr() + ":取值错误:" + value);
        } else {
            return SingleCheckerResult.success(null, checkInstance.fieldStr() + ":取值正确:" + value);
        }
    }
}