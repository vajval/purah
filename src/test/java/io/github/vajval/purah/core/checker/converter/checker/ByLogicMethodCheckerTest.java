package io.github.vajval.purah.core.checker.converter.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.checker.ComboBuilderChecker;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.util.People;
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

    Purahs purahs;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException {
        purahs = new Purahs(new PurahContext());
        Method method = ByLogicMethodCheckerTest.class.getMethod("nameNotEmpty", String.class);
        ByLogicMethodChecker methodChecker = new ByLogicMethodChecker(null, method, "nameNotEmpty",AutoNull.notEnable);
        purahs.reg(methodChecker);

        method = ByLogicMethodCheckerTest.class.getMethod("nameNotEmpty", People.class);
        methodChecker = new ByLogicMethodChecker(null, method, "nameNotEmpty",AutoNull.notEnable);
        purahs.reg(methodChecker);

    }


    @Test
    void doCheck() {
        Checker<Object, Object> checker = purahs.checkerOf("nameNotEmpty");

        Assertions.assertTrue(checker.check("123"));
        Assertions.assertTrue(checker.check(People.elder));

        Assertions.assertFalse(checker.check(""));
        Assertions.assertFalse(checker.check(new People()));


        ComboBuilderChecker comboBuilderChecker = purahs.combo("nameNotEmpty").match(new GeneralFieldMatcher("child#0.name"), "nameNotEmpty");

        Assertions.assertTrue(comboBuilderChecker.check(People.elder));


        Assertions.assertFalse(comboBuilderChecker.check(new People()));// no name
        Assertions.assertTrue(comboBuilderChecker.check(People.elder));//String
        Assertions.assertFalse(comboBuilderChecker.check(People.grandson));// no child


    }

    @Test
    public void nullTest() {
        Checker<Object, Object> checker = purahs.checkerOf("nameNotEmpty");
        CheckResult<?> result = checker.check(InputToCheckerArg.of(null, String.class));
        Assertions.assertTrue(result.log().contains("java.lang.String"));
        result = checker.check(InputToCheckerArg.of(null, People.class));
        Assertions.assertTrue(result.log().contains(People.class.getName()));


    }


}