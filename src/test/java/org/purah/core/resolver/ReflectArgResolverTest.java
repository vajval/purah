package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.TestObject;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.WildCardMatcher;

import java.util.*;

class ReflectArgResolverTest {

    TestObject testObject = TestObject.create();
    ReflectArgResolver reflectArgResolver = new ReflectArgResolver();

    @Test
    void getMatchFieldObjectMap() {
//        org.apache.commons.beanutils.converters.BooleanConverter
        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");

        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(testObject, wildCardMatcher);
        Assertions.assertEquals(matchFieldObjectMap.size(), 1);
        Assertions.assertEquals(matchFieldObjectMap.get("ab").argValue(), testObject.ab);
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



    @Test//    @Test
//    void fields() {
//        Set<String> fields = reflectArgResolver.fields(testObject);
//        Assertions.assertEquals(fields, Sets.newHashSet("a", "ab", "abc", "map", "child"));
//    }

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


        Log log = LogFactory.getLog(ReflectArgResolverTest.class);

        log.info("You do not want to see me");
        TestObject object = TestObject.create();

        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher(
                "ma*.mapKe*.key"
        );
//        Map<String, InputToCheckerArg<?>> thisLevelMatcherObjectMap = reflectArgResolver.getMultiLevelMap(InputToCheckerArg.of(object), generalFieldMatcher);
    }

    @Test
    void support() {
//        Assertions.assertTrue(reflectArgResolver.support(List.class));
//        Assertions.assertTrue(reflectArgResolver.support(Map.class));
//        Assertions.assertTrue(reflectArgResolver.support(Collection.class));
//        Assertions.assertTrue(reflectArgResolver.support(TestObject.class));

    }

    @Test
    void fieldMethodMap() {

    }


}