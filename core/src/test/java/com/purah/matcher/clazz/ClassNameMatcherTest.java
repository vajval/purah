package com.purah.matcher.clazz;

import com.purah.resolver.DefaultArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.purah.Util.*;
public class ClassNameMatcherTest {

    ClassNameMatcher matcher = new ClassNameMatcher( User.class.getName());



    @Test
    void test() {
        Assertions.assertTrue(matcher.match("initiator", trade));
        Assertions.assertTrue(matcher.match("recipients", trade));
        Assertions.assertFalse(matcher.match("money", trade));
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

