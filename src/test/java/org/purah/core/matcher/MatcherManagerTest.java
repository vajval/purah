package org.purah.core.matcher;

import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.matcher.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.clazz.AnnTypeFieldMatcherTest;
import org.purah.core.matcher.clazz.ClassNameMatcher;
import org.purah.core.matcher.clazz.ClassNameMatcherTest;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcherTest;

public class MatcherManagerTest {

    public static MatcherManager defaultMatcherManager = new MatcherManager();

    static {
        defaultMatcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        defaultMatcherManager.regBaseStrMatcher(ClassNameMatcher.class);
        defaultMatcherManager.regBaseStrMatcher(WildCardMatcher.class);

    }

    @Test
    void main() {

        MatcherManager matcherManager = defaultMatcherManager;

        FieldMatcher fieldMatcher = matcherManager.factoryOf("type_by_ann").create("需要检测");
        AnnTypeFieldMatcherTest.assertMatch(fieldMatcher);


        fieldMatcher = matcherManager.factoryOf("class_name").create(Util.User.class.getName());
        ClassNameMatcherTest.assertMatch( fieldMatcher);

        MatcherFactory matcherFactory = matcherManager.factoryOf("wild_card");
        WildCardMatcherTest.assertMatch_X(matcherFactory.create("a*"));
        WildCardMatcherTest.assertMatch_W(matcherFactory.create("a?"));

    }
}