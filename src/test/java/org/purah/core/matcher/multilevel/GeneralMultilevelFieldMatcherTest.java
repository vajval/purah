package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.resolver.DefaultArgResolver;


import java.util.Map;

class GeneralMultilevelFieldMatcherTest {

    @Test
    void match() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*i*.i*");
        Map<String, InputCheckArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalFieldMatcher);
        Assertions.assertEquals(map.get("initiator.id").inputArg(), Util.trade.getInitiator().getId());
        Assertions.assertEquals(map.get("recipients.id").inputArg(), Util.trade.getRecipients().getId());
    }

    @Test
    void match2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("in*.nam?");
        Map<String, InputCheckArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(Util.trade, generalFieldMatcher);
        Assertions.assertEquals(map.get("initiator.name").inputArg(), Util.trade.getInitiator().getName());
    }

}