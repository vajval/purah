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

}