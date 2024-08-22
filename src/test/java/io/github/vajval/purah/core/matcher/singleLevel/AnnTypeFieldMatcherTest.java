package io.github.vajval.purah.core.matcher.singleLevel;

import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import io.github.vajval.purah.util.People;

import java.util.Map;

public class AnnTypeFieldMatcherTest {


    @Test
    void resolver2() {
        ReflectArgResolver defaultArgResolver = new ReflectArgResolver();
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.oGetMatchFieldObjectMap(People.elder, new AnnTypeFieldMatcher("shortText"));
        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());

    }


}