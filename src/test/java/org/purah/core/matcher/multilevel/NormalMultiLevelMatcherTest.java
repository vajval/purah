package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.DefaultArgResolver;
import org.purah.util.People;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

class NormalMultiLevelMatcherTest {

    @Test
    void wrapChildMatcher() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();

        NormalMultiLevelMatcher generalFieldMatcher = new NormalMultiLevelMatcher("child#0.name|test|child#3.name|child#2.name");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = defaultArgResolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher);

        System.out.println(matchFieldObjectMap.keySet());
    }

    @Test
    void match() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();

        FixedMatcher generalFieldMatcher = new FixedMatcher("child#3.name");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = defaultArgResolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher);

        System.out.println(matchFieldObjectMap.keySet());
        System.out.println(matchFieldObjectMap.values());
    }

    @Test
    void matchFields() {
        DefaultArgResolver defaultArgResolver = new DefaultArgResolver();
//        [child#1.child, child#3.name, child#1.name.name, child#0.child, child#1.id, child#2.id, child#2.child, child#0.address, child#1.name, child#0.id, child#2.name, child#1.address, child#0.name, child#2.address]


        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("" +
                "child#*.name|child#3.name|child#1.name.name|child#*.name.name|child#*.id|child#*.name");


        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = defaultArgResolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher);


        ArrayList<String> strings = new ArrayList<>(matchFieldObjectMap.keySet());

        strings.sort(Comparator.comparing(String::trim));
        for (String string : strings) {
            System.out.println(string);
        }
        System.out.println(matchFieldObjectMap.keySet());
        System.out.println(matchFieldObjectMap.values());
    }

    @Test
    void childFieldMatcher() {
    }

    @Test
    void multilevelMatchInfoByChild() {
    }
}