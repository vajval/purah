package org.purah.core.checker.combinatorial;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.Util;
import org.purah.core.checker.BaseChecker;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.Checker;
import org.purah.core.checker.Checkers;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.CombinatorialCheckResult;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.clazz.ClassNameMatcher;
import org.purah.core.matcher.multilevel.GeneralMultilevelFieldMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;


import java.util.LinkedHashMap;

class CombinatorialCheckerTest {


    PurahContext purahContext;


    @BeforeEach
    public void beforeEach() {
        purahContext = new PurahContext();
        purahContext.matcherManager().regBaseStrMatcher(AnnTypeFieldMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(ClassNameMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(WildCardMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(GeneralMultilevelFieldMatcher.class);

        purahContext.checkManager().addCheckerFactory(
                new CheckerFactory() {
                    @Override
                    public boolean match(String needMatchCheckerName) {
                        return needMatchCheckerName.startsWith("id为");
                    }

                    @Override
                    public Checker createChecker(String needMatchCheckerName) {
                        Long id = Long.parseLong(needMatchCheckerName.replace("id为", ""));
                        return Checkers.autoStringChecker(needMatchCheckerName, i -> i.equals(id), Number.class);
                    }


                }
        );


        purahContext.checkManager().addCheckerFactory(
                new CheckerFactory() {
                    @Override
                    public boolean match(String needMatchCheckerName) {
                        return needMatchCheckerName.startsWith("必须姓");
                    }

                    @Override
                    public Checker createChecker(String needMatchCheckerName) {
                        String namePre = needMatchCheckerName.replace("必须姓", "");
                        return Checkers.<String>autoStringChecker(needMatchCheckerName, str
                                -> str.startsWith(namePre), String.class);
                    }


                }
        );

        purahContext.checkManager().reg(
                new BaseChecker<String, String>() {
                    @Override
                    public CheckResult<String> doCheck(CheckInstance<String> checkInstance) {
                        if (checkInstance.instance().contains("sb")) {
                            return failed(checkInstance, "有敏感词");
                        } else {
                            return success(checkInstance, "没有敏感词");
                        }
                    }

                    @Override
                    public String name() {
                        return "敏感词检测";
                    }


                }
        );
    }


    /*
     * 下面的 properties也可以通过 配置文件来编写
     * - name: 交易检测
     *     mapping:
     *        general:
     *           "[initia*.i*]":  id为1
     *           "[*ator.nam?]": 必须姓李
     *        type_by_ann:
     *              短文本: 敏感词检测
     */

    @Test
    public void test() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓张");

        properties.addByStrMap("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");

        properties.addByStrMap("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckResult checkerResult = (CombinatorialCheckResult) checker.check(CheckInstance.create(Util.trade));
        for (CheckResult result : checkerResult.value()) {
            System.out.println(result);
        }

        Assertions.assertFalse(checkerResult.isSuccess());
        Assertions.assertEquals(2, checkerResult.value().size());
    }

    @Test
    public void tes2() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");

        properties.addByStrMap("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.all_success_but_must_check_all);
        properties.addByStrMap("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckResult checkerResult = (CombinatorialCheckResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertFalse(checkerResult.isSuccess());
        for (CheckResult result : checkerResult.value()) {
            System.out.println(result);
        }
        Assertions.assertEquals(checkerResult.value().size(), 4);
    }

    @Test
    public void tes3() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");
        properties.addByStrMap("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.at_least_one);
        properties.addByStrMap("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckResult checkerResult = (CombinatorialCheckResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 1);

    }

    @Test
    public void tes4() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");

        properties.addByStrMap("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.at_least_one_but_must_check_all);
        properties.addByStrMap("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckResult checkerResult = (CombinatorialCheckResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        for (CheckResult result : checkerResult.value()) {
            System.out.println(result);
        }
        System.out.println(checkerResult.mainCheckResult());
        Assertions.assertEquals(0, checkerResult.value().size());

    }

//    @Test
//    public void tes6() {
//        for (int i = 0; i < 10000000; i++) {
//            tes5();
//        }
//    }

    public void tes5() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");

        properties.addByStrMap("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.at_least_one_but_must_check_all);
        properties.addByStrMap("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckResult checkerResult = (CombinatorialCheckResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 3);

    }
}