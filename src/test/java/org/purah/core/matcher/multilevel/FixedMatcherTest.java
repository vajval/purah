package org.purah.core.matcher.multilevel;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.LambdaChecker;
import org.purah.core.resolver.DefaultArgResolver;
import org.purah.util.People;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FixedMatcherTest {
    DefaultArgResolver resolver = new DefaultArgResolver();


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

        PurahContext purahContext = new PurahContext();
        purahContext.checkManager().reg(LambdaChecker.of(Object.class).build("notNull", Objects::nonNull));
        ComboBuilderChecker checker = purahContext.combo().match(fixedMatcher, "notNull");
        //noExistField child#5.child#0.id is null
        Assertions.assertFalse(checker.check(People.elder));


    }

}