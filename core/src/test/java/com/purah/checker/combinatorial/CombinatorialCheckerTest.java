package com.purah.checker.combinatorial;

import com.purah.PurahContext;
import com.purah.Util;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.Checkers;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.clazz.AnnTypeFieldMatcher;
import com.purah.matcher.clazz.ClassNameMatcher;
import com.purah.matcher.multilevel.GeneralMultilevelFieldMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

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
                    public CheckerResult<String> doCheck(CheckInstance<String> checkInstance) {
                        if (checkInstance.instance().contains("sb")) {
                            return failed("有敏感词");
                        } else {
                            return success("没有敏感词");
                        }
                    }

                    @Override
                    public String name() {
                        return "敏感词检测";
                    }


                }
        );
    }


    /**
     * 下面的 properties也可以通过 配置文件来编写
     * - name: 交易检测
     * mapping:
     * general:
     * "[initia*.i*]":  id为1
     * "[*ator.nam?]": 必须姓李
     * type_by_ann:
     * 短文本: 敏感词检测
     */

    @Test
    public void test() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓张");

        properties.add("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");

        properties.add("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 3);
    }

    @Test
    public void tes2() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");

        properties.add("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.all_success_but_must_check_all);
        properties.add("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertFalse(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 3);
    }

    @Test
    public void tes3() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");

        properties.add("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.at_least_one);
        properties.add("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 1);

    }

    @Test
    public void tes4() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("交易检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("initia*.i*", "id为1");
        map.put("*ator.nam?", "必须姓李");

        properties.add("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.at_least_one_but_must_check_all);
        properties.add("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 3);

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

        properties.add("general", map);

        map = new LinkedHashMap<>();
        map.put("短文本", "敏感词检测");
        properties.setMainExecType(ExecType.Main.at_least_one_but_must_check_all);
        properties.add("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);
        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult) checker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(checkerResult.isSuccess());
        Assertions.assertEquals(checkerResult.value().size(), 3);

    }
}