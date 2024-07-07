package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.LambdaChecker;
import org.purah.util.People;
import org.purah.core.Util;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.DefaultArgResolver;


import java.util.Map;
import java.util.Objects;


public class GeneralMultilevelFieldMatcherTest {


    @Test
    void generalFieldMatcher() {
        DefaultArgResolver resolver = new DefaultArgResolver();
        GeneralFieldMatcher normalMatcher = new GeneralFieldMatcher("na*|address|noExistField|child#*.id|child#5.child#5.id|child#*.child#4.id");

        Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(People.elder, normalMatcher);


        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());
        Assertions.assertEquals(map.get("address").argValue(), People.elder.getAddress());
        Assertions.assertNull(map.get("noExistField").argValue());
        Assertions.assertEquals(map.get("child#0.id").argValue(), People.elder.getChild().get(0).getId());
        Assertions.assertEquals(map.get("child#1.id").argValue(), People.elder.getChild().get(1).getId());
        Assertions.assertEquals(map.get("child#2.id").argValue(), People.elder.getChild().get(2).getId());

        Assertions.assertNull(map.get("child#5.child#5.id").argValue());

        Assertions.assertEquals(map.size(), 7);


    }

    @Test
    void match0() {


        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.child#*");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(objectMap.get("child#0.child#0").argEquals(People.grandson));
        Assertions.assertTrue(objectMap.get("child#1.child#0").argEquals(People.grandsonForDaughter));
        Assertions.assertTrue(objectMap.get("child#0.child#1").argEquals(People.granddaughter));
        Assertions.assertTrue(objectMap.get("child#1.child#1").argEquals(People.granddaughterForDaughter));

    }

    @Test
    void match1() {


        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.child#*.name");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(objectMap.get("child#0.child#0.name").argEquals(People.grandson.getName()));
        Assertions.assertTrue(objectMap.get("child#1.child#0.name").argEquals(People.grandsonForDaughter.getName()));
        Assertions.assertTrue(objectMap.get("child#0.child#1.name").argEquals(People.granddaughter.getName()));
        Assertions.assertTrue(objectMap.get("child#1.child#1.name").argEquals(People.granddaughterForDaughter.getName()));

    }

    @Test
    void match2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*i*.i*");
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalFieldMatcher);
        Assertions.assertEquals(map.get("initiator.id").argValue(), Util.trade.getInitiator().getId());
        Assertions.assertEquals(map.get("recipients.id").argValue(), Util.trade.getRecipients().getId());
    }

    @Test
    void match3() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("in*.nam?");
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalFieldMatcher);
        Assertions.assertEquals(map.get("initiator.name").argValue(), Util.trade.getInitiator().getName());
    }


}