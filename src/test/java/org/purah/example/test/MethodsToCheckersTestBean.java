//package org.purah.example.test;
//
//
//import org.purah.core.name.Name;
//import org.purah.core.checker.AbstractBaseSupportCacheChecker;
//import org.purah.core.checker.InputToCheckerArg;
//import org.purah.core.checker.Checker;
//import org.purah.core.checker.result.CheckResult;
//import org.purah.core.checker.result.LogicCheckResult;
//import org.purah.springboot.ioc.ann.PurahMethodsRegBean;
//import org.purah.springboot.ioc.ann.ToChecker;
//import org.purah.springboot.ioc.ann.ToCheckerFactory;
//import org.springframework.util.StringUtils;
//
//
//
//@PurahMethodsRegBean//将这个对象的函数转化为核对器
//public class MethodsToCheckersTestBean {
//
//    @ToChecker
//    @Name( "非空判断FromTestBean")
//    public boolean notEmpty(Object o) {
//        return o != null;
//    }
//    @ToCheckerFactory(match = "1取值必须在[*-*]之间判断FromTestBean")
//    public boolean range(String name, Number value) {
//
//        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
//        double min = Double.parseDouble(split[0]);
//        double max = Double.parseDouble(split[1]);
//        return value.doubleValue() >= min && value.doubleValue() <= max;
//    }
//
//
//    @ToChecker
//    @Name("有文本判断FromTestBean")
//    public CheckResult hasTest(String text) {
//        if (StringUtils.hasText(text)) {
//            return LogicCheckResult.success(null, "有文本");
//        } else {
//            return LogicCheckResult.failed(null, "无文本");
//        }
//    }
//
//
//
//    @ToCheckerFactory(match = "2取值必须在[*-*]之间判断FromTestBean")
//    public CheckResult range2(String name, Number value) {
//
//        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
//        double min = Double.parseDouble(split[0]);
//        double max = Double.parseDouble(split[1]);
//        boolean success = value.doubleValue() >= min && value.doubleValue() <= max;
//
//        if (success) {
//            return LogicCheckResult.success();
//        }
//        return LogicCheckResult.failed(null, name + "取值错误" + value);
//    }
//
//    @ToCheckerFactory(match = "3取值必须在[*-*]之间判断FromTestBean")
//    public Checker range3(String name) {
//
//        return new AbstractBaseSupportCacheChecker<Number, Object>() {
//            @Override
//            public CheckResult doCheck(InputToCheckerArg<Number> inputToCheckerArg) {
//                return range2(name, inputToCheckerArg.argValue());
//            }
//        };
//    }
//
//    @Name("数值判断FromTestBean")
//    @ToChecker
//    public CheckResult range(InputToCheckerArg<Number> inputToCheckerArg) {
//        int value = inputToCheckerArg.argValue().intValue();
//        if (value < 0) {
//            return LogicCheckResult.failed(null, inputToCheckerArg.fieldStr() + ":取值错误:" + value);
//        } else {
//            return LogicCheckResult.success(null, inputToCheckerArg.fieldStr() + ":取值正确:" + value);
//        }
//    }
//}