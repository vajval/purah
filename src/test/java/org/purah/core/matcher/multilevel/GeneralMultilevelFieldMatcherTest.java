package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.util.People;
import org.purah.core.Util;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.DefaultArgResolver;


import java.util.Map;


public class GeneralMultilevelFieldMatcherTest {

    @Test
    void math() {

        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#0.name");


    }

    @Test
    void match1() {

        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("{name,child#0.name,child#0.child#*.name}");
        System.out.println(generalFieldMatcher.childFieldMatcher(People.of("长者"),"name",People.of("长者").getName()));

//
//        System.out.println(()child.match("#0"));
        Map<String, InputToCheckerArg<People>> objectMap = (Map) defaultArgResolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher);
        System.out.println(objectMap.keySet());


    }

    @Test
    void match11() {
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.child#*.name");
//        FieldMatcher fieldMatcher = generalFieldMatcher.childFieldMatcher("child", null);
    }

    @Test
    void match22() {


        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.child#*.name");

        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher);
        System.out.println(objectMap);

        Assertions.assertTrue(objectMap.get("child#0.child#0.name").argEquals("孙子"));
        Assertions.assertTrue(objectMap.get("child#1.child#0.name").argEquals("外孙子"));
        Assertions.assertTrue(objectMap.get("child#0.child#1.name").argEquals("孙女"));
        Assertions.assertTrue(objectMap.get("child#1.child#1.name").argEquals("外孙女"));

    }

    @Test
    void match() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*i*.i*");
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalFieldMatcher);
        Assertions.assertEquals(map.get("initiator.id").argValue(), Util.trade.getInitiator().getId());
        Assertions.assertEquals(map.get("recipients.id").argValue(), Util.trade.getRecipients().getId());
    }

    @Test
    void match2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("in*.nam?");
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalFieldMatcher);
        System.out.println(map);
        Assertions.assertEquals(map.get("initiator.name").argValue(), Util.trade.getInitiator().getName());
    }



}