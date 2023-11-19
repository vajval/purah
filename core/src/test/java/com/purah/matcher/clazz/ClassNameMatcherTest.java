package com.purah.matcher.clazz;

import com.purah.matcher.intf.FieldMatcherWithInstance;
import com.purah.resolver.DefaultArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.purah.Util.*;

public class ClassNameMatcherTest {

    ClassNameMatcher matcher = new ClassNameMatcher(User.class.getName());


    @Test
    void test() {
        assertMatch(matcher);
    }


    public static void assertMatch(FieldMatcherWithInstance fieldMatcher) {
        Assertions.assertTrue(fieldMatcher.match("initiator", trade));
        Assertions.assertTrue(fieldMatcher.match("recipients", trade));
        Assertions.assertFalse(fieldMatcher.match("money", trade));
    }

    @Test
    void resolver() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, Object> map = defaultArgResolver.getMatchFieldObjectMap(trade, matcher);
        Assertions.assertEquals(map.get("initiator"), initiator);
        Assertions.assertEquals(map.get("recipients"), recipients);
        Assertions.assertNull(map.get("money"));
    }
}

