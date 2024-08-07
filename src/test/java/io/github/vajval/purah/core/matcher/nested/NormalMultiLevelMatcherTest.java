package io.github.vajval.purah.core.matcher.nested;

import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.ComboBuilderChecker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.LambdaChecker;
import io.github.vajval.purah.core.resolver.DefaultArgResolver;
import io.github.vajval.purah.util.People;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.PurahContext;

import java.util.Map;
import java.util.Objects;

class NormalMultiLevelMatcherTest {

    @Test
    void normalMultiLevelMatcher() {

        DefaultArgResolver resolver = new DefaultArgResolver();
        NormalMultiLevelMatcher normalMatcher = new NormalMultiLevelMatcher("name|address|noExistField|child#0.id|child#5.child#0.id");
        Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(People.elder, normalMatcher);
        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());
        Assertions.assertEquals(map.get("address").argValue(), People.elder.getAddress());
        Assertions.assertEquals(map.get("child#0.id").argValue(), People.elder.getChild().get(0).getId());
        Assertions.assertEquals(map.size(), 3);
        Assertions.assertFalse(map.containsKey("noExistField"));
        Assertions.assertFalse(map.containsKey("child#5.child#0.id"));
        Purahs purahs=new Purahs(new PurahContext());
        purahs.reg(LambdaChecker.of(Object.class).build("notNull", Objects::nonNull));
        ComboBuilderChecker checker = purahs.combo().match(normalMatcher, "notNull");
        //noExistField child#5.child#0.id not exist
        Assertions.assertTrue(checker.oCheck(People.elder));

    }


}