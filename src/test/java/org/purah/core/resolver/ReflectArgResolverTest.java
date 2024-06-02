package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.matcher.ann.FieldType;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ReflectArgResolverTest {

    TestObject testObject = TestObject.create();
    ReflectArgResolver reflectArgResolver = new ReflectArgResolver();

    @Test
    void getMatchFieldObjectMap() {
        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");

        Map<String, CheckInstance<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(testObject, wildCardMatcher);
        Assertions.assertEquals(matchFieldObjectMap.size(), 1);
        Assertions.assertEquals(matchFieldObjectMap.get("ab").instance(), testObject.ab);
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
        Assertions.assertEquals(fields, Sets.newHashSet("a", "ab", "abc"));
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
    void support() {
        Assertions.assertTrue(reflectArgResolver.support(List.class));
        Assertions.assertTrue(reflectArgResolver.support(Map.class));
        Assertions.assertTrue(reflectArgResolver.support(Collection.class));
        Assertions.assertTrue(reflectArgResolver.support(TestObject.class));

    }

    @Test
    void fieldMethodMap() {

    }

    public static class TestObject {
        @FieldType("123")
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