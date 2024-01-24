package com.purah.matcher.multilevel;

import com.purah.Util;
import com.purah.checker.CheckInstance;
import com.purah.resolver.DefaultArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.purah.Util.trade;
import static org.junit.jupiter.api.Assertions.*;

class GeneralMultilevelFieldMatcherTest {

    @Test
    void match() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralMultilevelFieldMatcher generalMultilevelFieldMatcher = new GeneralMultilevelFieldMatcher("*i*.i*");
        Map<String, CheckInstance> map = defaultArgResolver.getMatchFieldObjectMap(trade, generalMultilevelFieldMatcher);
        Assertions.assertEquals(map.get("initiator.id").instance(), trade.getInitiator().getId());
        Assertions.assertEquals(map.get("recipients.id").instance(), trade.getRecipients().getId());
    }

    @Test
    void match2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        GeneralMultilevelFieldMatcher generalMultilevelFieldMatcher = new GeneralMultilevelFieldMatcher("in*.nam?");
        Map<String, CheckInstance> map = defaultArgResolver.getMatchFieldObjectMap(trade, generalMultilevelFieldMatcher);
        Assertions.assertEquals(map.get("initiator.name").instance(), trade.getInitiator().getName());
    }

}