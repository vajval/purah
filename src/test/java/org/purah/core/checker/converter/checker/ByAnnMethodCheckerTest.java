package org.purah.core.checker.converter.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.base.Name;
import org.purah.core.checker.Checker;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.matcher.WildCardMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.resolver.ReflectArgResolver;
import org.purah.util.People;
import org.purah.util.TestAnn;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class ByAnnMethodCheckerTest {

    @Name("test")
    public static boolean testByName(TestAnn testAnn, String name) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        if (testAnn == null) {
            return false;
        }
        return StringUtils.hasText(testAnn.value());
    }


    @Name("test")
    public static boolean testByPeople(TestAnn testAnn, People people) {
        if (people == null) {
            return false;
        }

        return testByName(testAnn, people.getName());
    }

    PurahContext purahContext;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException {
        purahContext = new PurahContext();
        Method method = ByAnnMethodCheckerTest.class.getMethod("testByName", TestAnn.class, String.class);
        ByAnnMethodChecker byAnnMethodChecker = new ByAnnMethodChecker(null, method, "test");
        purahContext.checkManager().reg(byAnnMethodChecker);

        method = ByAnnMethodCheckerTest.class.getMethod("testByPeople", TestAnn.class, People.class);
        byAnnMethodChecker = new ByAnnMethodChecker(null, method, "test");
        purahContext.checkManager().reg(byAnnMethodChecker);

    }


    @Test
    void name() {

        Checker checker = purahContext.combo().match(new WildCardMatcher("name"), "test");

        CheckResult check = checker.check(People.of("长者"));
        Assertions.assertTrue(check.isSuccess());
        checker = purahContext.combo("test");
        check = checker.check(People.of("长者"));
        Assertions.assertTrue(check.isFailed());
        ReflectArgResolver resolver = new ReflectArgResolver();
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("child#0.name");
        System.out.println(resolver.getMatchFieldObjectMap(People.of("长者"), generalFieldMatcher).keySet());
        checker = purahContext.combo().match(new GeneralFieldMatcher("child#0.name"), "test");
        check = checker.check(People.of("长者"));
        Assertions.assertTrue(check.isSuccess());

    }

    @Test
    void doCheck() {
    }

    @Test
    void errorMsgCheckerByAnnMethod() {
    }
}