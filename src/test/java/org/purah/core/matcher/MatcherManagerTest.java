package org.purah.core.matcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import org.purah.core.matcher.singlelevel.ClassNameMatcher;
import org.purah.core.matcher.singlelevel.WildCardMatcher;

public class MatcherManagerTest {

    @Test
    void reg() {
        MatcherManager matcherManager = new MatcherManager();


        matcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        matcherManager.regBaseStrMatcher(ClassNameMatcher.class);
        matcherManager.regBaseStrMatcher(WildCardMatcher.class);

        FieldMatcher fieldMatcher = matcherManager.factoryOf("type_by_ann").create("needCheck");
        Assertions.assertEquals(new AnnTypeFieldMatcher("needCheck"), fieldMatcher);

        fieldMatcher = matcherManager.factoryOf("class_name").create("java.lang.String");
        Assertions.assertEquals(new ClassNameMatcher("java.lang.String"), fieldMatcher);

        fieldMatcher = matcherManager.factoryOf("wild_card").create("a*");
        Assertions.assertEquals(new WildCardMatcher("a*"), fieldMatcher);

    }
}