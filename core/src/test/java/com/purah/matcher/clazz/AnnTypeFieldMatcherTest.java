package com.purah.matcher.clazz;

import com.purah.checker.CheckInstance;
import com.purah.matcher.intf.FieldMatcherWithInstance;
import com.purah.resolver.DefaultArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.purah.Util.*;

public class AnnTypeFieldMatcherTest {
    AnnTypeFieldMatcher matcher = new AnnTypeFieldMatcher("需要检测");

    @Test
    void test() {

        assertMatch(matcher);


    }


    public static void assertMatch(FieldMatcherWithInstance fieldMatcher) {
        Assertions.assertTrue(fieldMatcher.match("initiator", trade));
        Assertions.assertFalse(fieldMatcher.match("recipients", trade));
        Assertions.assertFalse(fieldMatcher.match("money", trade));
    }
    @Test
    void resolver2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, CheckInstance> map = defaultArgResolver.getMatchFieldObjectMap(trade,  new AnnTypeFieldMatcher("短文本"));
        Assertions.assertEquals(map.get("title").instance(), trade.getTitle());
        Assertions.assertNull(map.get("recipients"));
    }



    @Test
    void resolver() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, CheckInstance> map = defaultArgResolver.getMatchFieldObjectMap(trade, matcher);
        Assertions.assertEquals(map.get("initiator").instance(), initiator);
        Assertions.assertNull(map.get("recipients"));
    }

}