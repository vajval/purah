package org.purah.core.matcher.clazz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.resolver.DefaultArgResolver;

import java.util.Map;

public class ClassNameMatcherTest {

    ClassNameMatcher matcher = new ClassNameMatcher(Util.User.class.getName());


    @Test
    void test() {
        assertMatch(matcher);
    }


    public static void assertMatch(FieldMatcher fieldMatcher) {
        Assertions.assertTrue(fieldMatcher.match("initiator", Util.trade));
        Assertions.assertTrue(fieldMatcher.match("recipients", Util.trade));
        Assertions.assertFalse(fieldMatcher.match("money", Util.trade));
    }

    @Test
    void resolver() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, InputCheckArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, matcher);
        Assertions.assertEquals(map.get("initiator").inputArg(), Util.initiator);
        Assertions.assertEquals(map.get("recipients").inputArg(), Util.recipients);
        Assertions.assertNull(map.get("money"));
    }
}

