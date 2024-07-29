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

class FixedMatcherTest {
    final DefaultArgResolver resolver = new DefaultArgResolver();


    @Test
    void test() {
        FixedMatcher fixedMatcher = new FixedMatcher("child#5.child#0.id");

        Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(People.elder, fixedMatcher);
        Assertions.assertNull(map.get("child#5.child#0.id").argValue());
    }

    @Test
    void fixedMatcher() {


        FixedMatcher fixedMatcher = new FixedMatcher("name|address|noExistField|child#0.id|child#5.child#0.id");

        Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(People.elder, fixedMatcher);
        System.out.println(map);
        Assertions.assertNull(map.get("child#5.child#0.id").argValue());

        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());
        Assertions.assertEquals(map.get("address").argValue(), People.elder.getAddress());
        Assertions.assertEquals(map.get("child#0.id").argValue(), People.elder.getChild().get(0).getId());
        Assertions.assertNull(map.get("noExistField").argValue());
        Assertions.assertEquals(map.size(), 5);

        Purahs purahs=new Purahs(new PurahContext());
        purahs.reg(LambdaChecker.of(Object.class).build("notNull", Objects::nonNull));
        ComboBuilderChecker checker = purahs.combo().match(fixedMatcher, "notNull");
        //noExistField child#5.child#0.id is null
        Assertions.assertFalse(checker.check(People.elder));


    }

}