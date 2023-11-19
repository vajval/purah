package com.purah.matcher;

import com.purah.Util;
import com.purah.matcher.clazz.AnnTypeFieldMatcher;
import com.purah.matcher.clazz.AnnTypeFieldMatcherTest;
import com.purah.matcher.clazz.ClassNameMatcher;
import com.purah.matcher.clazz.ClassNameMatcherTest;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.intf.FieldMatcherWithInstance;
import com.purah.matcher.singleLevel.WildCardMatcher;
import com.purah.matcher.singleLevel.WildCardMatcherTest;
import org.junit.jupiter.api.Test;

class MatcherManagerTest {

    @Test
    void main() {
        MatcherManager matcherManager = new MatcherManager();

        matcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        matcherManager.regBaseStrMatcher(ClassNameMatcher.class);

        matcherManager.regBaseStrMatcher(WildCardMatcher.class);


        FieldMatcher fieldMatcher = matcherManager.factoryOf("type_by_ann").create("需要检测");
        AnnTypeFieldMatcherTest.assertMatch((FieldMatcherWithInstance) fieldMatcher);


        fieldMatcher = matcherManager.factoryOf("class_name").create(Util.User.class.getName());
        ClassNameMatcherTest.assertMatch((FieldMatcherWithInstance) fieldMatcher);

        MatcherFactory matcherFactory = matcherManager.factoryOf("wild_card");
        WildCardMatcherTest.assertMatch_X(matcherFactory.create("a*"));
        WildCardMatcherTest.assertMatch_W(matcherFactory.create("a?"));

    }
}