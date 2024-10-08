package io.github.vajval.purah.core.matcher.nested;

import com.google.common.collect.Lists;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.util.People;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;


import java.util.Map;

import static io.github.vajval.purah.util.User.GOOD_USER;
import static io.github.vajval.purah.util.User.GOOD_USER_GOOD_CHILD;


public class GeneralMultilevelFieldMatcherTest {

    @Test

    public void generalFieldMatche123122() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.*");
        Map<String, InputToCheckerArg<?>> stringInputToCheckerArgMap = resolver.oGetMatchFieldObjectMap(GOOD_USER_GOOD_CHILD, generalFieldMatcher);
        Assertions.assertEquals(stringInputToCheckerArgMap.size(), 10);
        stringInputToCheckerArgMap = resolver.oGetMatchFieldObjectMap(GOOD_USER, generalFieldMatcher);
        Assertions.assertEquals(stringInputToCheckerArgMap.size(), 5);
    }

    @Test

    public void generalFieldMatche12() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|*.*");
        Map<String, InputToCheckerArg<?>> stringInputToCheckerArgMap = resolver.oGetMatchFieldObjectMap(GOOD_USER_GOOD_CHILD, generalFieldMatcher);
        Assertions.assertEquals(stringInputToCheckerArgMap.size(), 10);
        stringInputToCheckerArgMap = resolver.oGetMatchFieldObjectMap(GOOD_USER, generalFieldMatcher);
        Assertions.assertEquals(stringInputToCheckerArgMap.size(), 5);
    }

    @Test
    public void generalFieldMatche12234323122() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|*.*");
        Map<String, InputToCheckerArg<?>> stringInputToCheckerArgMap = resolver.oGetMatchFieldObjectMap(GOOD_USER, generalFieldMatcher);
        Assertions.assertEquals(stringInputToCheckerArgMap.size(), 5);
        stringInputToCheckerArgMap = resolver.oGetMatchFieldObjectMap(GOOD_USER_GOOD_CHILD, generalFieldMatcher);
        Assertions.assertEquals(stringInputToCheckerArgMap.size(), 10);
    }

    @Test

    public void generalFieldMatcher3() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("#0.id");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(Lists.newArrayList(People.elder), generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("#0.id").argEquals(People.elder.getId()));
    }

    @Test
    public void generalFieldMatscher2() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.id");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child#0.id").argEquals(People.elder.getChild().get(0).getId()));

    }

    @Test

    public void generalFieldMatcher2() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("#0");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(Lists.newArrayList("123"), generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("#0").argEquals("123"));

        generalFieldMatcher = new GeneralFieldMatcher("name");
        matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("name").argEquals(People.elder.getName()));

        generalFieldMatcher = new GeneralFieldMatcher("child");
        matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child").argEquals(People.elder.getChild()));


        generalFieldMatcher = new GeneralFieldMatcher("child#0");
        matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child#0").argEquals(People.elder.getChild().get(0)));

        generalFieldMatcher = new GeneralFieldMatcher("child#*.id");
        matchFieldObjectMap = resolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(matchFieldObjectMap.get("child#0.id").argEquals(People.elder.getChild().get(0).getId()));
    }


    @Test
    void generalFieldMatcher() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher normalMatcher = new GeneralFieldMatcher("na*|address|noExistField|child#*.id|child#5.child#5.id|child#*.child#4.id");

        Map<String, InputToCheckerArg<?>> map = resolver.oGetMatchFieldObjectMap(People.elder, normalMatcher);

        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());
        Assertions.assertEquals(map.get("address").argValue(), People.elder.getAddress());
        Assertions.assertNull(map.get("noExistField").argValue());
        Assertions.assertEquals(map.get("child#0.id").argValue(), People.elder.getChild().get(0).getId());
        Assertions.assertEquals(map.get("child#1.id").argValue(), People.elder.getChild().get(1).getId());
        Assertions.assertEquals(map.get("child#2.id").argValue(), People.elder.getChild().get(2).getId());

        Assertions.assertNull(map.get("child#5.child#5.id").argValue());

        Assertions.assertEquals(7, map.size());
    }

    @Test
    void match2131() {

        ReflectArgResolver defaultArgResolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.name");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(objectMap.get("child#0.name").argEquals(People.son.getName()));


    }

    @Test
    void match123() {
        ReflectArgResolver defaultArgResolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);

        Assertions.assertTrue(objectMap.get("child#0").argEquals(People.son));

    }

    @Test
    void match0() {


        ReflectArgResolver defaultArgResolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.child#*");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);


        Assertions.assertTrue(objectMap.get("child#0.child#0").argEquals(People.grandson));
        Assertions.assertTrue(objectMap.get("child#1.child#0").argEquals(People.grandsonForDaughter));
        Assertions.assertTrue(objectMap.get("child#0.child#1").argEquals(People.granddaughter));
        Assertions.assertTrue(objectMap.get("child#1.child#1").argEquals(People.granddaughterForDaughter));

    }

    @Test
    void match1() {


        ReflectArgResolver defaultArgResolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#*.child#*.name");
        Map<String, InputToCheckerArg<?>> objectMap = defaultArgResolver.oGetMatchFieldObjectMap(People.elder, generalFieldMatcher);
        Assertions.assertTrue(objectMap.get("child#0.child#0.name").argEquals(People.grandson.getName()));
        Assertions.assertTrue(objectMap.get("child#1.child#0.name").argEquals(People.grandsonForDaughter.getName()));
        Assertions.assertTrue(objectMap.get("child#0.child#1.name").argEquals(People.granddaughter.getName()));
        Assertions.assertTrue(objectMap.get("child#1.child#1.name").argEquals(People.granddaughterForDaughter.getName()));

    }


}