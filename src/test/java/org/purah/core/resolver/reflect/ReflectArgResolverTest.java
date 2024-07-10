package org.purah.core.resolver.reflect;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.TestObject;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.multilevel.FixedMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.singlelevel.WildCardMatcher;
import org.purah.core.resolver.reflect.ReflectArgResolver;
import org.purah.example.customAnn.pojo.CustomUser;
import org.purah.util.People;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        CustomUser goodCustomUser = new CustomUser(3L, "vajva", "15509931234", 15);
        GeneralFieldMatcher fieldMatcher = new GeneralFieldMatcher("*.*");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        System.out.println(matchFieldObjectMap);

    }

    @Test
    void getMatchFieldObjectMap() {
        GeneralFieldMatcher fieldMatcher = new GeneralFieldMatcher("*|child.name");
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        System.out.println(matchFieldObjectMap);
        matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        System.out.println(matchFieldObjectMap);
        matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fieldMatcher);
        System.out.println(matchFieldObjectMap);
    }

    @Test
    void getMatchFieldObjectMapFromNull() {
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();

        FixedMatcher fixedMatcher = new FixedMatcher("child.child.name");
        Map<String, InputToCheckerArg<?>> map = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(testUser), fixedMatcher);
        Assertions.assertFalse(CollectionUtils.isEmpty(map.get("child.child.name").annListOnField()));
        map = reflectArgResolver.getMatchFieldObjectMap(InputToCheckerArg.of(null, TestUser.class), fixedMatcher);
        Assertions.assertFalse(CollectionUtils.isEmpty(map.get("child.child.name").annListOnField()));
    }




}