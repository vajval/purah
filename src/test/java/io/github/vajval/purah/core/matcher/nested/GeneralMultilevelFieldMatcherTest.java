package io.github.vajval.purah.core.matcher.nested;

import com.google.common.collect.Lists;
import io.github.vajval.purah.core.resolver.DefaultArgResolver;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.util.People;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;


import java.util.Map;

import static io.github.vajval.purah.util.User.GOOD_USER;
import static io.github.vajval.purah.util.User.GOOD_USER_BAD_CHILD;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class GeneralMultilevelFieldMatcherTest {


    @Test
    public void generalFieldMatcherqwe3() {
        DefaultArgResolver resolver = new DefaultArgResolver();

        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(GOOD_USER_BAD_CHILD, new GeneralFieldMatcher("*|*.*"));

        System.out.println(matchFieldObjectMap.keySet());
        resolver.getMatchFieldObjectMap(GOOD_USER_BAD_CHILD, new GeneralFieldMatcher("*|*.*"));

        System.out.println(matchFieldObjectMap.keySet());

    }

    @Test

    public void generalFieldMatcher3() {
        DefaultArgResolver resolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("#0.id");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(Lists.newArrayList(People.elder), generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("#0.id").argEquals(People.elder.getId()));
    }

    @Test

    public void generalFieldMatcher2() {
        DefaultArgResolver resolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("#0");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(Lists.newArrayList("123"), generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("#0").argEquals("123"));

        generalFieldMatcher = new GeneralFieldMatcher("name");
        matchFieldObjectMap = resolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("name").argEquals(People.elder.getName()));

        generalFieldMatcher = new GeneralFieldMatcher("child");
        matchFieldObjectMap = resolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child").argEquals(People.elder.getChild()));


        generalFieldMatcher = new GeneralFieldMatcher("child#0");
        matchFieldObjectMap = resolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child#0").argEquals(People.elder.getChild().get(0)));

        generalFieldMatcher = new GeneralFieldMatcher("child#*.id");
        matchFieldObjectMap = resolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child#0.id").argEquals(People.elder.getChild().get(0).getId()));
    }


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
    void match2131() {


        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.name");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.getMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(objectMap.get("child#0.name").argEquals(People.son.getName()));


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


}