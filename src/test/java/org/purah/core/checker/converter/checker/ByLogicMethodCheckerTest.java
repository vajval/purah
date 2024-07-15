package org.purah.core.checker.converter.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
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
        ComboBuilderChecker checker = purahContext.combo("nameNotEmpty");

        Assertions.assertTrue(checker.check("123"));
        Assertions.assertTrue(checker.check(People.elder));

        Assertions.assertFalse(checker.check(""));
        Assertions.assertFalse(checker.check(new People()));


        checker = purahContext.combo().match(new GeneralFieldMatcher("child#0.name"), "nameNotEmpty");

        Assertions.assertTrue(checker.check(People.elder));


        Assertions.assertFalse(checker.check(new People()));// no name
        Assertions.assertTrue(checker.check(People.elder));//String
        Assertions.assertFalse(checker.check(People.grandson));// no child



    }


}