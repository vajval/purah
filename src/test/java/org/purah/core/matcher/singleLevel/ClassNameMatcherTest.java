package org.purah.core.matcher.singleLevel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.singlelevel.ClassNameMatcher;
import org.purah.core.resolver.DefaultArgResolver;
import org.purah.util.People;

import java.util.Map;

public class ClassNameMatcherTest {


    @Test
    void resolver() {
        ClassNameMatcher matcher = new ClassNameMatcher(String.class.getName());

        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
        Map<String, InputToCheckerArg<?>> map = defaultArgResolver.getMatchFieldObjectMap(People.elder, matcher);
        Assertions.assertEquals(map.get("address").argValue(), People.elder.getAddress());
        Assertions.assertEquals(map.get("name").argValue(), People.elder.getName());
        Assertions.assertEquals(map.get("id").argValue(), People.elder.getId());
    }
}

