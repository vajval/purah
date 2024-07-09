package org.purah.core.matcher.singleLevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import org.purah.core.resolver.DefaultArgResolver;

import java.util.Map;

public class AnnTypeFieldMatcherTest {


    @Test
    void resolver2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, new AnnTypeFieldMatcher("needCheck"));
        Assertions.assertEquals(map.get("initiator").argValue(), Util.initiator);
        map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, new AnnTypeFieldMatcher("shortText"));
        Assertions.assertEquals(map.get("title").argValue(), Util.trade.getTitle());
    }


}