package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.ReflectArgResolver;
import org.purah.util.People;

import java.util.Map;

class OptionMatcherTest {


    @Test
    void test() {
        FixedMatcher fixedMatcher = new FixedMatcher("child#0.child#0.name|child#0|child#0.child|child#100|name|child#0.child#0.child|child");

        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(
                People.of("长者"), fixedMatcher
        );
        for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
            System.out.println("-------------");
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());

        }
        System.out.println(matchFieldObjectMap.keySet());

    }


}