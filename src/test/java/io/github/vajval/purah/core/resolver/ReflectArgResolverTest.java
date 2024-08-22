package io.github.vajval.purah.core.resolver;

import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.util.TestUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;

import java.util.*;

class ReflectArgResolverTest {

    TestUser testUser;
    ReflectArgResolver reflectArgResolver;

    @BeforeEach
    public void beforeEach() {
        reflectArgResolver = new ReflectArgResolver();
        testUser = new TestUser(1L, "name", "address");
        testUser.child = new TestUser(2L, "child_name", "child_address");
    }

    @Test
    void getMatchFieldObj() {
        GeneralFieldMatcher fieldMatcher = new GeneralFieldMatcher("*.*");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);

        Assertions.assertEquals(matchFieldObjectMap.size(), 4);
        Assertions.assertTrue(matchFieldObjectMap.containsKey("child.address"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("child.people"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("child.child"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("child.name"));

    }

    @Test
    void getMatchFieldObjectMap() {
        GeneralFieldMatcher fieldMatcher = new GeneralFieldMatcher("*|child.name");
        Map<String, InputToCheckerArg<?>> firstLevelMatcherObjectMap = reflectArgResolver.getFirstLevelMatcherObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        Assertions.assertEquals(firstLevelMatcherObjectMap.size(), 4);
        Assertions.assertTrue(firstLevelMatcherObjectMap.containsKey("name"));
        Assertions.assertTrue(firstLevelMatcherObjectMap.containsKey("address"));
        Assertions.assertTrue(firstLevelMatcherObjectMap.containsKey("people"));
        Assertions.assertTrue(firstLevelMatcherObjectMap.containsKey("child"));


        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);

        Assertions.assertEquals(matchFieldObjectMap.size(), 5);
        Assertions.assertTrue(matchFieldObjectMap.containsKey("address"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("name"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("people"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("child.name"));
        Assertions.assertTrue(matchFieldObjectMap.containsKey("child"));
    }


}