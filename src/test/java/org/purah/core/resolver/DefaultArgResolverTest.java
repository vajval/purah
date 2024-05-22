package org.purah.core.resolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.CheckInstance;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.util.Map;

class DefaultArgResolverTest {
    DefaultArgResolver defaultArgResolver = new DefaultArgResolver();


    @Test
    void getMatchFieldObjectMap() {
        MapStringObjectArgResolverTest.TestStringObjectMap testStringObjectMap = new MapStringObjectArgResolverTest.TestStringObjectMap();
        testStringObjectMap.put("a", "a");
        testStringObjectMap.put("ab", "ab");
        WildCardMatcher wildCardMatcher = new WildCardMatcher("?b");

        ReflectArgResolverTest.TestObject testObject = ReflectArgResolverTest.TestObject.create();


        Map<String, CheckInstance> getFromMap = defaultArgResolver.getMatchFieldObjectMap(testStringObjectMap,wildCardMatcher);

        Map<String, CheckInstance> getFromObject = defaultArgResolver.getMatchFieldObjectMap(testObject,wildCardMatcher);


        Assertions.assertEquals(getFromMap, getFromObject);

        Assertions.assertEquals(getFromMap.size(),1);
        Assertions.assertEquals(getFromMap.get("ab").instance(),"ab");

    }


}