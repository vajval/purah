package org.purah.core.matcher.clazz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.matcher.intf.FieldMatcherWithInstance;
import org.purah.core.resolver.DefaultArgResolver;

import java.util.Map;

public class ClassNameMatcherTest {

    ClassNameMatcher matcher = new ClassNameMatcher(Util.User.class.getName());


    @Test
    void test() {
        assertMatch(matcher);
    }


    public static void assertMatch(FieldMatcherWithInstance fieldMatcher) {
        Assertions.assertTrue(fieldMatcher.match("initiator", Util.trade));
        Assertions.assertTrue(fieldMatcher.match("recipients", Util.trade));
        Assertions.assertFalse(fieldMatcher.match("money", Util.trade));
    }

    @Test
    void resolver() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, CheckInstance<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, matcher);
        Assertions.assertEquals(map.get("initiator").instance(), Util.initiator);
        Assertions.assertEquals(map.get("recipients").instance(), Util.recipients);
        Assertions.assertNull(map.get("money"));
    }
}

