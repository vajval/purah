package org.purah.core.matcher.clazz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.resolver.DefaultArgResolver;

import java.util.Map;

public class AnnTypeFieldMatcherTest {
    AnnTypeFieldMatcher matcher = new AnnTypeFieldMatcher("需要检测");

    @Test
    void test() {

        assertMatch(matcher);


    }


    public static void assertMatch(FieldMatcher fieldMatcher) {
        Assertions.assertTrue(fieldMatcher.match("initiator", Util.trade));
        Assertions.assertFalse(fieldMatcher.match("recipients", Util.trade));
        Assertions.assertFalse(fieldMatcher.match("money", Util.trade));
    }
    @Test
    void resolver2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();



        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade,  new AnnTypeFieldMatcher("短文本"));

        AnnTypeFieldMatcher 短文本 = new AnnTypeFieldMatcher("短文本");



        Assertions.assertEquals(map.get("title").argValue(), Util.trade.getTitle());
        Assertions.assertNull(map.get("recipients"));
    }



    @Test
    void resolver() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, matcher);
        Assertions.assertEquals(map.get("initiator").argValue(), Util.initiator);
        Assertions.assertNull(map.get("recipients"));
    }

}