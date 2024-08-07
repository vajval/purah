package io.github.vajval.purah.core.matcher.singleLevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import io.github.vajval.purah.core.resolver.DefaultArgResolver;
import io.github.vajval.purah.util.People;

import java.util.Map;

public class AnnTypeFieldMatcherTest {


    @Test
    void resolver2() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(People.elder, new AnnTypeFieldMatcher("shortText"));
        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());

    }


}