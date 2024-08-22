package io.github.vajval.purah.core.resolver;

import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.util.TestUser;
import io.github.vajval.purah.util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassReflectCacheTest {

    FixedMatcher fixedMatcher;
    TestUser testUser;
    ClassReflectCache classReflectCache;

    @BeforeEach
    public void beforeEach() {
        testUser = new TestUser(1L, "name", "address");
        classReflectCache = new ClassReflectCache(TestUser.class, false);
        fixedMatcher = new FixedMatcher("id|name|address|child.name");
    }

    @Test
    public void cacheEnable() {
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        ClassReflectCache notEnableUnsafe = new ClassReflectCache(User.class, false);
        InputToCheckerArg<User> arg = InputToCheckerArg.of(User.GOOD_USER_BAD_CHILD);

        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
        Map<String, InputToCheckerArg<?>> result = reflectArgResolver.getMatchFieldObjectMap(arg, generalFieldMatcher);
        assertFalse(notEnableUnsafe.supportCache(arg, generalFieldMatcher, result));

        FixedMatcher fixedMatcher = new FixedMatcher("childUser.id");
        result = reflectArgResolver.getMatchFieldObjectMap(arg, fixedMatcher);
        assertFalse(notEnableUnsafe.supportCache(arg, fixedMatcher, result));

        generalFieldMatcher = new GeneralFieldMatcher("*");
        result = reflectArgResolver.getMatchFieldObjectMap(arg, generalFieldMatcher);
        assertTrue(notEnableUnsafe.supportCache(arg, generalFieldMatcher, result));

        ClassReflectCache enableUnsafe = new ClassReflectCache(User.class, true);
        generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
        result = reflectArgResolver.getMatchFieldObjectMap(arg, generalFieldMatcher);
        assertTrue(enableUnsafe.supportCache(arg, generalFieldMatcher, result));

        result = reflectArgResolver.getMatchFieldObjectMap(arg, fixedMatcher);
        assertTrue(enableUnsafe.supportCache(arg, fixedMatcher, result));


    }

    @Test
    public void matchValue() {
        testUser.child = new TestUser(2L, "child_name", "child_address");
        Map<String, InputToCheckerArg<?>> map = classReflectCache.thisLevelMatchFieldValueMap(InputToCheckerArg.of(testUser), fixedMatcher);
        Assertions.assertEquals(map.get("name").argValue(), testUser.name);
        Assertions.assertEquals(map.get("address").argValue(), testUser.address);
        Assertions.assertEquals(map.get("child").argValue(), testUser.child);

        Map<String, InputToCheckerArg<?>> mapFromNull = classReflectCache.thisLevelMatchFieldValueMap(InputToCheckerArg.of(null, TestUser.class), fixedMatcher);
        Assertions.assertEquals(map.get("name").annListOnField(), mapFromNull.get("name").annListOnField());

        Map<String, InputToCheckerArg<?>> mapFromEmpty = ClassReflectCache.nullOrEmptyValueReflectCache.thisLevelMatchFieldValueMap(InputToCheckerArg.of(null), fixedMatcher);
        Assertions.assertEquals(map.keySet().size(), mapFromEmpty.keySet().size());
        assertTrue(mapFromEmpty.get("child.name").isNull());
    }

}