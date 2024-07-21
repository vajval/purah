package org.purah.core.checker.converter.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.checker.Checker;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.name.Name;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.matcher.nested.GeneralFieldMatcher;
import org.purah.util.People;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;


public class ByLogicMethodCheckerTest {
    @Name("nameNotEmpty")
    public static boolean nameNotEmpty(String name) {

        return StringUtils.hasText(name);
    }


    @Name("nameNotEmpty")
    public static boolean nameNotEmpty(People people) {
        if (people == null) return false;
        return StringUtils.hasText(people.getName());
    }

    PurahContext purahContext;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException {
        purahContext = new PurahContext();
        Method method = ByLogicMethodCheckerTest.class.getMethod("nameNotEmpty", String.class);
        ByLogicMethodChecker methodChecker = new ByLogicMethodChecker(null, method, "nameNotEmpty");
        purahContext.checkManager().reg(methodChecker);

        method = ByLogicMethodCheckerTest.class.getMethod("nameNotEmpty", People.class);
        methodChecker = new ByLogicMethodChecker(null, method, "nameNotEmpty");
        purahContext.checkManager().reg(methodChecker);

    }


    @Test
    void doCheck() {
        GenericsProxyChecker checker = purahContext.checkManager().of("nameNotEmpty");

        Assertions.assertTrue(checker.check("123"));
        Assertions.assertTrue(checker.check(People.elder));

        Assertions.assertFalse(checker.check(""));
        Assertions.assertFalse(checker.check(new People()));


        ComboBuilderChecker comboBuilderChecker = purahContext.combo("nameNotEmpty").match(new GeneralFieldMatcher("child#0.name"), "nameNotEmpty");

        Assertions.assertTrue(comboBuilderChecker.check(People.elder));


        Assertions.assertFalse(comboBuilderChecker.check(new People()));// no name
        Assertions.assertTrue(comboBuilderChecker.check(People.elder));//String
        Assertions.assertFalse(comboBuilderChecker.check(People.grandson));// no child


    }

    @Test
    public void nullTest() {
        GenericsProxyChecker checker = purahContext.checkManager().of("nameNotEmpty");
        CheckResult<?> result = checker.check(InputToCheckerArg.of(null, String.class));
        Assertions.assertTrue(result.log().contains("java.lang.String"));
        result = checker.check(InputToCheckerArg.of(null, People.class));
        Assertions.assertTrue(result.log().contains(People.class.getName()));


    }


}