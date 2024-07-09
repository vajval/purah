package org.purah.core.matcher.singleLevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.singlelevel.ClassNameMatcher;
import org.purah.core.resolver.DefaultArgResolver;

import java.util.Map;

public class ClassNameMatcherTest {



    @Test
    void resolver() {
        ClassNameMatcher matcher = new ClassNameMatcher(Util.User.class.getName());

        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, matcher);
        Assertions.assertEquals(map.get("initiator").argValue(), Util.initiator);
        Assertions.assertEquals(map.get("recipients").argValue(), Util.recipients);
        Assertions.assertNull(map.get("money"));
    }
}

