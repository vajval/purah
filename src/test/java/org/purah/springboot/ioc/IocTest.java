package org.purah.springboot.ioc;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.purah.core.PurahContext;
import org.purah.core.matcher.FieldMatcher;
import org.purah.springboot.ioc.test_bean.checker.IocIgnoreChecker;
import org.purah.springboot.ioc.test_bean.checker.IocMethodRegTestBean;
import org.purah.springboot.ioc.test_bean.checker.IocTestChecker;
import org.purah.springboot.ioc.test_bean.matcher.IocIgnoreMatcher;
import org.purah.springboot.ioc.test_bean.matcher.ReverseStringMatcher;
import org.purah.springboot.ioc.test_bean.matcher.ReverseStringMatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = ExampleApplication.class)
public class IocTest {
    @Autowired
    PurahContext purahContext;


    @Test
    public void ioc() {

        Assertions.assertTrue(containsChecker(IocTestChecker.NAME));
        Assertions.assertTrue(containsChecker(IocMethodRegTestBean.NOT_NULL_CHECKER_NAME));
        Assertions.assertTrue(containsChecker(IocMethodRegTestBean.RANGE_TEST));

        Assertions.assertFalse(containsChecker(IocIgnoreChecker.NAME));


        Assertions.assertTrue(containsMatcherFactory(ReverseStringMatcher.NAME));
        Assertions.assertTrue(containsMatcherFactory(ReverseStringMatcherFactory.NAME));
        Assertions.assertFalse(containsMatcherFactory(IocIgnoreMatcher.NAME));


    }

    @Test
    public void test() {
        FieldMatcher fieldMatcher = purahContext.matcherManager().factoryOf(ReverseStringMatcher.NAME).create("123|abc");
        Set<String> strings = fieldMatcher.matchFields(Sets.newHashSet("123", "321", "abc", "cba"), null);
        Assertions.assertEquals(strings, Sets.newHashSet("321", "cba"));

        fieldMatcher = purahContext.matcherManager().factoryOf(ReverseStringMatcher.NAME).create("123|abc");
        strings = fieldMatcher.matchFields(Sets.newHashSet("123", "321", "abc", "cba"), null);
        Assertions.assertEquals(strings, Sets.newHashSet("321", "cba"));
    }


    public boolean containsMatcherFactory(String factory) {

        try {
            purahContext.matcherManager().factoryOf(factory);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean containsChecker(String name) {
        try {
            purahContext.checkManager().of(name);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
