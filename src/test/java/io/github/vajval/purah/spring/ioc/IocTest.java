package io.github.vajval.purah.spring.ioc;

import com.google.common.collect.Sets;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.spring.ioc.test_bean.PurahConfigPropertiesBean;
import io.github.vajval.purah.spring.ioc.test_bean.TestCallBack;
import io.github.vajval.purah.spring.ioc.test_bean.checker.IocIgnoreChecker;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.IocIgnoreMatcher;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcherFactory;
import io.github.vajval.purah.util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.ExampleApplication;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.spring.ioc.test_bean.checker.IocMethodRegTestBean;
import io.github.vajval.purah.spring.ioc.test_bean.checker.IocTestChecker;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = ExampleApplication.class)
public class IocTest {
    @Autowired
    Purahs purahs;
    @Autowired
    PurahConfigPropertiesBean purahConfigPropertiesBean;


    @Test
    public void ioc() {

        Assertions.assertTrue(containsChecker(IocTestChecker.NAME));
        Assertions.assertTrue(containsChecker(IocMethodRegTestBean.NOT_NULL_CHECKER_NAME));
        Assertions.assertTrue(containsChecker(IocMethodRegTestBean.RANGE_TEST));

        Assertions.assertFalse(containsChecker(IocIgnoreChecker.NAME));


        Assertions.assertTrue(containsMatcherFactory(ReverseStringMatcher.NAME));
        Assertions.assertTrue(containsMatcherFactory(ReverseStringMatcherFactory.NAME));
        Assertions.assertFalse(containsMatcherFactory(IocIgnoreMatcher.NAME));
        Assertions.assertEquals(TestCallBack.value, 1);
        Assertions.assertDoesNotThrow(() -> purahs.checkerOf("user_reg"));
        System.out.println(purahConfigPropertiesBean);
        CheckResult<Object> result = purahs.checkerOf("用户注册检查").check(new User(null, null, null, null));
        System.out.println(result);

    }

    @Test
    public void test() {
        FieldMatcher fieldMatcher = purahs.matcherOf(ReverseStringMatcher.NAME).create("abc");
        Set<String> strings = fieldMatcher.matchFields(Sets.newHashSet("123", "321", "abc", "cba"), null);
        Assertions.assertEquals(strings, Sets.newHashSet( "cba"));

        fieldMatcher = purahs.matcherOf(ReverseStringMatcher.NAME).create("123");
        strings = fieldMatcher.matchFields(Sets.newHashSet("123", "321"), null);
        Assertions.assertEquals(strings, Sets.newHashSet("321"));
    }


    public boolean containsMatcherFactory(String factory) {

        try {
            return purahs.matcherOf(factory) != null;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean containsChecker(String name) {
        try {
            return purahs.checkerOf(name) != null;
        } catch (Exception e) {
            return false;
        }

    }

}
