package com.purah.checker.combinatorial;

import com.google.common.collect.Maps;
import com.purah.PurahContext;
import com.purah.Util;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.Checkers;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.clazz.AnnTypeFieldMatcher;
import com.purah.matcher.clazz.ClassNameMatcher;
import com.purah.matcher.multilevel.GeneralMultilevelFieldMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

class CombinatorialCheckerConfigTest {
    PurahContext purahContext = new PurahContext();

    @BeforeEach
    public void beforeEach() {
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
    }

//    }

    /*
     * - name: 用户id检测
     *    field_check_mapping:
     *          wild_card:
     * ___________"i*": id为1
     */


    @Test
    void user_id_check() {


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("用户id检测");
        LinkedHashMap<@Nullable String, @Nullable String> map = Maps.newLinkedHashMap();
        map.put("i*", "id为1");
        properties.addByStrMap("wild_card", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);


        CheckerResult result = checker.check(CheckInstance.create(Util.initiator));
        Assertions.assertTrue(result.isSuccess());

        result = checker.check(CheckInstance.create(Util.recipients));
        Assertions.assertFalse(result.isSuccess());
    }

    /*
     * - name: 交易发起者id检测
     *    field_check_mapping:
     *        general:
     *           "i*.i*": id为1
     */


    @Test
    void initiator_id_check() {

        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("test_properties");
        LinkedHashMap<@Nullable String, @Nullable String> multiLevelMap = Maps.newLinkedHashMap();
        multiLevelMap.put("i*.i*", "id为1");
        properties.addByStrMap("general", multiLevelMap);


        Checker multiLevelchecker = purahContext.regNewCombinatorialChecker(properties);


        CheckerResult result = multiLevelchecker.check(CheckInstance.create(Util.trade));
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void setMainExecType() {
    }

    @Test
    void setExtendCheckerNames() {
    }

    @Test
    void setName() {
    }

    @Test
    void addMatcherCheckerName() {
    }
}