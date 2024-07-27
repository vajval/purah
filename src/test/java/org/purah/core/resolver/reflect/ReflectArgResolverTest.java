package org.purah.core.resolver.reflect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.nested.FixedMatcher;
import org.purah.core.matcher.nested.GeneralFieldMatcher;
import org.purah.core.resolver.ReflectArgResolver;
import org.purah.util.User;
import org.purah.util.TestUser;

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
        User goodUser = new User(3L, "vajva", "15509931234", 15);
        GeneralFieldMatcher fieldMatcher = new GeneralFieldMatcher("*.*");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        System.out.println(matchFieldObjectMap);

    }

    @Test
    void getMatchFieldObjectMap() {
        GeneralFieldMatcher fieldMatcher = new GeneralFieldMatcher("*|child.name");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
    }



    @Test
    void cache() {
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        FixedMatcher fixedMatcher = new FixedMatcher("child.child.name");
        Map<String, InputToCheckerArg<?>> map = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fixedMatcher);

        map = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fixedMatcher);
    }

}