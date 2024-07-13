package org.purah.example.test;


import org.purah.core.name.Name;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.Checker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.springboot.ann.PurahMethodsRegBean;
import org.purah.springboot.ann.convert.ToChecker;
import org.purah.springboot.ann.convert.ToCheckerFactory;
import org.springframework.util.StringUtils;



@PurahMethodsRegBean//将这个对象的函数转化为核对器
public class MethodsToCheckersTestBean {

    @ToChecker
    @Name( "非空判断FromTestBean")
    public boolean notEmpty(Object o) {
        return o != null;
    }
    @ToCheckerFactory(match = "1取值必须在[*-*]之间判断FromTestBean")
    public boolean range(String name, Number value) {

        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }


    @ToChecker
    @Name("有文本判断FromTestBean")
    public CheckResult hasTest(String text) {
        if (StringUtils.hasText(text)) {
            return BaseLogicCheckResult.success(null, "有文本");
        } else {
            return BaseLogicCheckResult.failed(null, "无文本");
        }
    }



    @ToCheckerFactory(match = "2取值必须在[*-*]之间判断FromTestBean")
    public CheckResult range2(String name, Number value) {

        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        boolean success = value.doubleValue() >= min && value.doubleValue() <= max;

        if (success) {
            return BaseLogicCheckResult.success();
        }
        return BaseLogicCheckResult.failed(null, name + "取值错误" + value);
    }

    @ToCheckerFactory(match = "3取值必须在[*-*]之间判断FromTestBean")
    public Checker range3(String name) {

        return new AbstractBaseSupportCacheChecker<Number, Object>() {
            @Override
            public CheckResult doCheck(InputToCheckerArg<Number> inputToCheckerArg) {
                return range2(name, inputToCheckerArg.argValue());
            }
        };
    }

    @Name("数值判断FromTestBean")
    @ToChecker
    public CheckResult range(InputToCheckerArg<Number> inputToCheckerArg) {
        Integer value = inputToCheckerArg.argValue().intValue();
        if (value < 0) {
            return BaseLogicCheckResult.failed(null, inputToCheckerArg.fieldStr() + ":取值错误:" + value);
        } else {
            return BaseLogicCheckResult.success(null, inputToCheckerArg.fieldStr() + ":取值正确:" + value);
        }
    }
}