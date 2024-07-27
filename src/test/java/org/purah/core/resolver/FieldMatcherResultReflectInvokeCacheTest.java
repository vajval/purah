package org.purah.core.resolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.ann.CNPhoneNum;
import org.purah.core.checker.ann.NotEmptyTest;
import org.purah.core.checker.ann.NotNull;
import org.purah.core.checker.ann.Range;
import org.purah.core.matcher.nested.FixedMatcher;
import org.purah.core.matcher.nested.GeneralFieldMatcher;
import org.purah.util.People;
import org.purah.util.User;

import java.util.Map;


class FieldMatcherResultReflectInvokeCacheTest {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    public Long id;
    @NotEmptyTest(errorMsg = "this field cannot empty")
    public String name;
    @CNPhoneNum(errorMsg = "phone num wrong")
    public String phone;


    @NotNull(errorMsg = "norBull")
    public Integer age;

    User childUser;
    @Test
    void invokeResultByCache() {
        DefaultArgResolver resolver = new DefaultArgResolver();
        FixedMatcher fixedMatcher = new FixedMatcher("id|name|childUser.id");
        Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(User.GOOD_USER_BAD_CHILD, fixedMatcher);

        FieldMatcherResultReflectInvokeCache fieldMatcherResultReflectInvokeCache = new FieldMatcherResultReflectInvokeCache(

                User.class, fixedMatcher, map
        );
        Map<String, InputToCheckerArg<?>> invokeMap = fieldMatcherResultReflectInvokeCache.invokeResultByCache(User.GOOD_USER_BAD_CHILD);
        System.out.println(map);
        System.out.println(invokeMap);
        for (Map.Entry<String, InputToCheckerArg<?>> argEntry : map.entrySet()) {
            Assertions.assertEquals(invokeMap.get(argEntry.getKey()), argEntry.getValue());
        }
    }
}