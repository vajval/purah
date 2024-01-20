package com.purah.resolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.purah.Util;
import com.purah.matcher.singleLevel.WildCardMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReflectArgResolverTest {

    TestObject testObject = TestObject.create();
    ReflectArgResolver reflectArgResolver = new ReflectArgResolver();

    @Test
    void getMatchFieldObjectMap() {
        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");

        Map<String, Object> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(testObject, wildCardMatcher);
        Assertions.assertEquals(matchFieldObjectMap.size(), 1);
        Assertions.assertEquals(matchFieldObjectMap.get("ab"), testObject.ab);
    }


    @Test
    void getObject() {
        Object getValue = reflectArgResolver.getObject(testObject, "a");
        Assertions.assertEquals(testObject.a, getValue);

    }


    @Test
    void fields() {
        Set<String> fields = reflectArgResolver.fields(testObject);
        Assertions.assertEquals(fields, Sets.newHashSet("a", "ab", "abc"));
    }

    @Test
    void matchFieldList() {
//        WildCardMatcher matcher = new WildCardMatcher("i*");
//
//        Set<String> s = reflectArgResolver.matchFieldList(Util.initiator, matcher);
//        System.out.println(s);


        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");

        Set<String> matchFieldList = reflectArgResolver.matchFieldList(testObject, wildCardMatcher);

        Assertions.assertEquals(matchFieldList, Sets.newHashSet("ab"));
    }

    @Test
    void support() {
        Assertions.assertFalse(reflectArgResolver.support(List.class));
        Assertions.assertFalse(reflectArgResolver.support(Map.class));
        Assertions.assertFalse(reflectArgResolver.support(Collection.class));
        Assertions.assertTrue(reflectArgResolver.support(TestObject.class));

    }

    @Test
    void fieldMethodMap() {

    }

  public   static class TestObject {
        protected String a;

        protected String ab;

        protected String abc;

        public static TestObject create() {
            TestObject testObject = new TestObject();
            testObject.a = "a";
            testObject.ab = "ab";
            testObject.abc = "abc";
            return testObject;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getAb() {
            return ab;
        }

        public void setAb(String ab) {
            this.ab = ab;
        }

        public String getAbc() {
            return abc;
        }

        public void setAbc(String abc) {
            this.abc = abc;
        }
    }
}