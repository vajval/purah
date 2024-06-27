package org.purah.core.checker.combinatorial;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.Util;
import org.purah.core.checker.*;
import org.purah.core.checker.factory.LambdaCheckerFactory;
import org.purah.core.checker.result.CombinatorialCheckResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.clazz.ClassNameMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.WildCardMatcher;


import java.util.LinkedHashMap;

class CombinatorialCheckerTest {


    PurahContext purahContext;


    @BeforeEach
    public void beforeEach() {
        purahContext = new PurahContext();
        purahContext.matcherManager().regBaseStrMatcher(AnnTypeFieldMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(ClassNameMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(WildCardMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(GeneralFieldMatcher.class);
        purahContext.checkManager().addCheckerFactory(
                LambdaCheckerFactory.of(Number.class).build(i -> i.startsWith("id为"),
                        (name, inputArg) -> {
                            Long id = Long.parseLong(name.replace("id为", ""));
                            return inputArg.equals(id);
                        })
        );
        purahContext.checkManager().addCheckerFactory(
                LambdaCheckerFactory.of(String.class).build(i -> i.startsWith("必须姓"),
                        (name, inputArg) -> {
                            String namePre = name.replace("必须姓", "");
                            return inputArg.startsWith(namePre);
                        })

        );



        purahContext.checkManager().reg(
                LambdaChecker.of(String.class).build("敏感词检测", i -> !i.contains("sb"))
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
        CombinatorialCheckResult combinatorialCheckResult = (CombinatorialCheckResult) checker.check(Util.trade);

        Assertions.assertFalse(combinatorialCheckResult.isSuccess());
        Assertions.assertEquals(1, combinatorialCheckResult.data().size());
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
        CombinatorialCheckResult CheckResult = (CombinatorialCheckResult) checker.check(Util.trade);
        Assertions.assertFalse(CheckResult.isSuccess());
        System.out.println(CheckResult.data());

        Assertions.assertEquals(CheckResult.data().size(), 2);
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
        properties.setResultLevel(ResultLevel.all);
        properties.addByStrMap("type_by_ann", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);


        CombinatorialCheckResult CheckResult = (CombinatorialCheckResult) checker.check(Util.trade);
        Assertions.assertTrue(CheckResult.isSuccess());
        Assertions.assertEquals(CheckResult.data().size(), 1);

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
        CombinatorialCheckResult checkResult = (CombinatorialCheckResult) checker.check(Util.trade);
        Assertions.assertTrue(checkResult.isSuccess());
        System.out.println(checkResult.data());

        Assertions.assertEquals(2, checkResult.data().size());

    }


    @Test
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
        CombinatorialCheckResult checkResult = (CombinatorialCheckResult) checker.check(Util.trade);

        Assertions.assertTrue(checkResult.isSuccess());
        Assertions.assertEquals(checkResult.data().size(), 2);

    }
}