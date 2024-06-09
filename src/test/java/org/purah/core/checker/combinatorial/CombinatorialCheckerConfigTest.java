package org.purah.core.checker.combinatorial;

import com.google.common.collect.Maps;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.Util;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.Checkers;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.factory.CheckerFactory;

import org.purah.core.matcher.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.clazz.ClassNameMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;


import java.util.LinkedHashMap;

class CombinatorialCheckerConfigTest {
    PurahContext purahContext = new PurahContext();

    @BeforeEach
    public void beforeEach() {
        purahContext.matcherManager().regBaseStrMatcher(AnnTypeFieldMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(ClassNameMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(WildCardMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(GeneralFieldMatcher.class);

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


        CombinatorialCheckerConfigBuilder properties = new CombinatorialCheckerConfigBuilder("用户id检测");
        LinkedHashMap<@Nullable String, @Nullable String> map = Maps.newLinkedHashMap();
        map.put("i*", "id为1");
        properties.addByStrMap("wild_card", map);


        Checker checker = purahContext.regNewCombinatorialChecker(properties);


        CheckResult result = checker.check(InputCheckArg.create(Util.initiator));
        Assertions.assertTrue(result.isSuccess());

        result = checker.check(InputCheckArg.create(Util.recipients));
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

        CombinatorialCheckerConfigBuilder properties = new CombinatorialCheckerConfigBuilder("test_properties");
        LinkedHashMap<@Nullable String, @Nullable String> multiLevelMap = Maps.newLinkedHashMap();
        multiLevelMap.put("i*.i*", "id为1");
        properties.addByStrMap("general", multiLevelMap);


        Checker multiLevelchecker = purahContext.regNewCombinatorialChecker(properties);


        CheckResult result = multiLevelchecker.check(InputCheckArg.create(Util.trade));
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