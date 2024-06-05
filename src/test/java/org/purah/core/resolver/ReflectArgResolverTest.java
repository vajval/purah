package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.TestObject;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

class ReflectArgResolverTest {

    TestObject testObject = TestObject.create();
    ReflectArgResolver reflectArgResolver = new ReflectArgResolver();

    @Test
    void getMatchFieldObjectMap() {
        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");

        Map<String, InputCheckArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(testObject, wildCardMatcher);
        Assertions.assertEquals(matchFieldObjectMap.size(), 1);
        Assertions.assertEquals(matchFieldObjectMap.get("ab").inputArg(), testObject.ab);
    }


//    @Test
//    void getObject() {
//        CheckInstance<Object> checkInstance = reflectArgResolver.getCheckInstance(testObject, "a");
//        Assertions.assertEquals(testObject.a, checkInstance.instance());
//
//        FieldType fieldType = checkInstance.annOf(FieldType.class);
//
//        Assertions.assertEquals(Arrays.stream(fieldType.value()).findFirst().get(), "123");
//
//    }


    @Test
    void fields() {
        Set<String> fields = reflectArgResolver.fields(testObject);
        Assertions.assertEquals(fields, Sets.newHashSet("a", "ab", "abc", "map", "child"));
    }

    @Test
    void matchFieldList() {


//
//        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");
//
//        Set<String> matchFieldList = reflectArgResolver.matchFieldList(testObject, wildCardMatcher);
//
//        Assertions.assertEquals(matchFieldList, Sets.newHashSet("ab"));
    }

    @Test
    void matchst() {


        TestObject object = TestObject.create();

        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher(
                "ma*.mapKe*.key"
        );
        Map<String, InputCheckArg<?>> thisLevelMatcherObjectMap = reflectArgResolver.getMultiLevelMap(object, generalFieldMatcher);
        System.out.println(thisLevelMatcherObjectMap);
    }

    @Test
    void support() {
        Assertions.assertTrue(reflectArgResolver.support(List.class));
        Assertions.assertTrue(reflectArgResolver.support(Map.class));
        Assertions.assertTrue(reflectArgResolver.support(Collection.class));
        Assertions.assertTrue(reflectArgResolver.support(TestObject.class));

    }

    @Test
    void fieldMethodMap() {

    }


}