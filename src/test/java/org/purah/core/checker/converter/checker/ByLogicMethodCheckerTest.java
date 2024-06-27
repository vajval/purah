package org.purah.core.checker.converter.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.base.Name;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.util.People;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ByLogicMethodCheckerTest {
    @Name("nameNotEmpty")
    public static boolean nameNotEmpty(String name) {
        System.out.println(name);
        return StringUtils.hasText(name);
    }


    @Name("nameNotEmpty")
    public static boolean nameNotEmpty(People people) {
        System.out.println(people);
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
        Assertions.assertTrue(checker.check("123").isSuccess());
        Assertions.assertFalse(checker.check("").isSuccess());

        Assertions.assertFalse(checker.check(new People()).isSuccess());
        Assertions.assertTrue(checker.check(People.of("长者")).isSuccess());


        checker = purahContext.combo().match(new GeneralFieldMatcher("child#0.name"), "nameNotEmpty");
        Assertions.assertTrue(checker.check(People.of("长者")).isSuccess());
        System.out.println(People.of("孙子"));
        Assertions.assertFalse(checker.check(People.of("孙子")).isSuccess());

        Assertions.assertTrue(checker.check(new People()).isSuccess());
        Assertions.assertFalse(checker.check(People.of("长者")).isSuccess());
    }

    @Test
    void errorMsgCheckerByLogicMethod() {
    }
}