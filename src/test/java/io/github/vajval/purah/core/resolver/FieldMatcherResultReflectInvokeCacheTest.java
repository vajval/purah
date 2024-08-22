package io.github.vajval.purah.core.resolver;

import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.ann.CNPhoneNum;
import io.github.vajval.purah.core.checker.ann.NotEmptyTest;
import io.github.vajval.purah.core.checker.ann.NotNull;
import io.github.vajval.purah.core.checker.ann.Range;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.util.User;

import java.util.HashMap;
import java.util.Map;


class FieldMatcherResultReflectInvokeCacheTest {


    @Test
    void invokeResultByCache() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        FixedMatcher fixedMatcher = new FixedMatcher("id|name|childUser.id");
        Map<String, InputToCheckerArg<?>> map = resolver.oGetMatchFieldObjectMap(User.GOOD_USER_BAD_CHILD, fixedMatcher);
        FieldMatcherResultReflectInvokeCache fieldMatcherResultReflectInvokeCache = new FieldMatcherResultReflectInvokeCache(
                User.class, fixedMatcher, map
        );
        Map<String, InputToCheckerArg<?>> invokeMap = fieldMatcherResultReflectInvokeCache.invokeResultByCache(User.GOOD_USER_BAD_CHILD);
        for (Map.Entry<String, InputToCheckerArg<?>> argEntry : map.entrySet()) {
            Assertions.assertEquals(invokeMap.get(argEntry.getKey()), argEntry.getValue());
        }
    }

    @Test
    void tree() {
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher fixedMatcher = new GeneralFieldMatcher("*|*.*");
        Map<String, InputToCheckerArg<?>> map = resolver.oGetMatchFieldObjectMap(User.GOOD_USER_BAD_CHILD, fixedMatcher);
        FieldMatcherResultReflectInvokeCache cache = new FieldMatcherResultReflectInvokeCache(User.class, fixedMatcher, map);
        Map<String, InputToCheckerArg<?>> cacheResult = new HashMap<>();
        cache.reflectTrieCache.invoke(User.GOOD_USER_BAD_CHILD,cacheResult);
        Assertions.assertEquals(cacheResult,map);
        cacheResult = new HashMap<>();
        cache.reflectTrieCache.invoke(User.GOOD_USER_GOOD_CHILD,cacheResult);
        Assertions.assertNotEquals(cacheResult,map);
    }
}