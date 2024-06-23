package org.purah.core.matcher.multilevel;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.resolver.DefaultArgResolver;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class GeneralMultilevelFieldMatcherTest {

    @Test
    void match1() {
//
//        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("{child#0,child#0.child#1}.name");
//        System.out.println(generalFieldMatcher);
//        System.out.println("-------------");
//        generalFieldMatcher = (GeneralFieldMatcher) generalFieldMatcher.childFieldMatcher("child", null);
//        System.out.println(111111);
//        System.out.println(generalFieldMatcher);
//
//        generalFieldMatcher = (GeneralFieldMatcher) generalFieldMatcher.childFieldMatcher("#0", null);
//        System.out.println(222222);
//        System.out.println(generalFieldMatcher);
//        System.out.println(generalFieldMatcher.match("name"));
////        FieldMatcher child = generalFieldMatcher.childFieldMatcher("child");
////
////        System.out.println(()child.match("#0"));
//        Map<String, InputToCheckerArg<People>> objectMap = (Map) defaultArgResolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher);
//        System.out.println(objectMap.keySet());
//        System.out.println(objectMap.get("child").fieldStr());
//        System.out.println(objectMap.get("child").argValue());


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

    public static class People {

        String id;
        String name;
        List<People> child;

        public People(String name) {
            this.name = name;
            this.id = UUID.randomUUID().toString();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<People> getChild() {
            return child;
        }

        public void setChild(List<People> child) {
            this.child = child;
        }

        @Override
        public String toString() {
            return "People{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", child=" + child +
                    '}';
        }


        public static People of(String name) {
            return testPeople.get(name);
        }

        public static Map<String, People> testPeople = testPeople();

        public static Map<String, People> testPeople() {
            Map<String, People> map = Lists.newArrayList("长者", "儿子", "女儿", "孙子", "孙女", "外孙子", "外孙女").stream().map(People::new).collect(Collectors.toMap(People::getName, i -> i));
            map.get("长者").setChild(Lists.newArrayList(map.get("儿子"), map.get("女儿")));
            map.get("儿子").setChild(Lists.newArrayList(map.get("孙子"), map.get("孙女")));
            map.get("女儿").setChild(Lists.newArrayList(map.get("外孙子"), map.get("外孙女")));
            return map;

        }
    }
}