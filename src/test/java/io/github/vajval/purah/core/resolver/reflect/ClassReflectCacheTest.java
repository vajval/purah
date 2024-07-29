package io.github.vajval.purah.core.resolver.reflect;

import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.core.matcher.nested.NormalMultiLevelMatcher;
import io.github.vajval.purah.core.resolver.ClassReflectCache;
import io.github.vajval.purah.util.TestUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassReflectCacheTest {

    FixedMatcher cacheableFixedMatcher;
    NormalMultiLevelMatcher noCacheNormalMatcher;
    TestUser testUser;
    ClassReflectCache classReflectCache;
    final AtomicInteger count = new AtomicInteger(0);


    @BeforeEach
    public void beforeEach() {
        count.set(0);
        testUser = new TestUser(1L, "name", "address");
        classReflectCache = new ClassReflectCache(TestUser.class);
        cacheableFixedMatcher = new FixedMatcher("id|name|address|child.name") {
            @Override
            public Set<String> matchFields(Set<String> fields, Object belongInstance) {
                count.addAndGet(1);
                return super.matchFields(fields, belongInstance);
            }
        };
        noCacheNormalMatcher = new NormalMultiLevelMatcher("id|name|address") {
            @Override
            public Set<String> matchFields(Set<String> fields, Object belongInstance) {
                count.addAndGet(1);
                return super.matchFields(fields, belongInstance);
            }

            @Override
            public boolean supportCache() {
                return false;
            }
        };
    }


    @Test
    void getMatchFieldObjectMapFromNull() {
//        ReflectArgResolver resolver = new ReflectArgResolver();
//        GeneralFieldMatcher fixedMatcher = new GeneralFieldMatcher("address|child.id");
//        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(InputToCheckerArg.of(null, TestUser.class), fixedMatcher);
//
//        boolean tryRegNewCache = classReflectCache.tryRegNewCache(InputToCheckerArg.of(null, TestUser.class), fixedMatcher, matchFieldObjectMap);
//        Assertions.assertTrue(tryRegNewCache);
//
//        ClassReflectCache reflectCache = resolver.classClassConfigCacheMap.get(TestUser.class);
//
//
//        Map<String, InputToCheckerArg<?>> stringInputToCheckerArgMap = reflectCache.byCache(testUser, fixedMatcher);
//
//        System.out.println(stringInputToCheckerArgMap);
//
//        testUser.child = new TestUser(2L, "child_name", "child_address");
//
//        Map<String, InputToCheckerArg<?>> map = reflectCache.byCache(testUser, fixedMatcher);
//        System.out.println(map);
    }


    @Test
    public void matchValue() {
        testUser.child = new TestUser(2L, "child_name", "child_address");
        Map<String, InputToCheckerArg<?>> map = classReflectCache.thisLevelMatchFieldValueMap(InputToCheckerArg.of(testUser), cacheableFixedMatcher);
        Assertions.assertEquals(map.get("name").argValue(), testUser.name);
        Assertions.assertEquals(map.get("address").argValue(), testUser.address);
        Assertions.assertEquals(map.get("child").argValue(), testUser.child);

        Map<String, InputToCheckerArg<?>> mapFromNull = classReflectCache.thisLevelMatchFieldValueMap(InputToCheckerArg.of(null, TestUser.class), cacheableFixedMatcher);
        Assertions.assertEquals(map.get("name").annListOnField(), mapFromNull.get("name").annListOnField());

        Map<String, InputToCheckerArg<?>> mapFromEmpty = ClassReflectCache.nullOrEmptyValueReflectCache.thisLevelMatchFieldValueMap(InputToCheckerArg.of(null), cacheableFixedMatcher);
        Assertions.assertEquals(map.keySet().size(), mapFromEmpty.keySet().size());
        Assertions.assertTrue(mapFromEmpty.get("child.name").isNull());
    }

//    @Test
//    public void noCacheTest() {
//        for (int i = 0; i < 100; i++) {
//            Assertions.assertEquals(classReflectCache.matchFieldList(testUser, noCacheNormalMatcher).size(), 2);
//        }
//        Assertions.assertEquals(count.get(), 100);
//    }

//    @Test
//    public void cacheTest() {
//        for (int i = 0; i < 100; i++) {
//            Assertions.assertEquals(classReflectCache.matchFieldList(null, cacheableFixedMatcher).size(), 4);
//            Assertions.assertEquals(classReflectCache.matchFieldList(testUser, cacheableFixedMatcher).size(), 4);
//        }
//        Assertions.assertEquals(count.get(), 1);
//    }


}