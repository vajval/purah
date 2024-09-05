package io.github.vajval.purah.performance;

import com.google.common.collect.Maps;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.resolver.FieldMatcherResultReflectInvokeCache;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.core.resolver.ReflectUtils;
import io.github.vajval.purah.util.User;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectTest {
    @Test
    public void generalWithCache() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = User.class.getMethod("getName");
        User user = User.GOOD_USER_BAD_CHILD;
        Object s = method.invoke(user);

        System.out.println(s);
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        reflectArgResolver.enableExtendUnsafeCache(true);
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
        Map<String, InputToCheckerArg<?>> stringInputToCheckerArgMap = reflectArgResolver.oGetMatchFieldObjectMap(User.GOOD_USER_BAD_CHILD, generalFieldMatcher);
        FieldMatcherResultReflectInvokeCache.ReflectTrieCache reflectTrieCache
                = new FieldMatcherResultReflectInvokeCache.ReflectTrieCache("");
        for (Map.Entry<String, InputToCheckerArg<?>> entry : stringInputToCheckerArgMap.entrySet()) {
            reflectTrieCache.insert(entry.getKey(), entry.getValue(), entry.getKey());


        }
        for (int i = 0; i < 1_0_000_000; i++) {
            reflectTrieCache.invoke(user,Maps.newHashMapWithExpectedSize(20));

        }
//

    }
}
