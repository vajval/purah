package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.resolver.DefaultArgResolver;


import java.util.Map;

class GeneralMultilevelFieldMatcherTest {

    @Test
    void match() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralMultilevelFieldMatcher generalMultilevelFieldMatcher = new GeneralMultilevelFieldMatcher("*i*.i*");
        Map<String, CheckInstance<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalMultilevelFieldMatcher);
        Assertions.assertEquals(map.get("initiator.id").instance(), Util.trade.getInitiator().getId());
        Assertions.assertEquals(map.get("recipients.id").instance(), Util.trade.getRecipients().getId());
    }

    @Test
    void match2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralMultilevelFieldMatcher generalMultilevelFieldMatcher = new GeneralMultilevelFieldMatcher("in*.nam?");
        Map<String, CheckInstance<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalMultilevelFieldMatcher);
        Assertions.assertEquals(map.get("initiator.name").instance(), Util.trade.getInitiator().getName());
    }

}