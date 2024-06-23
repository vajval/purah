package org.purah.core.checker.method.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.base.Name;
import org.purah.core.checker.method.ByLogicMethodChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class CheckerByLogicMethodTest {


    @Name("numTest")
    protected static boolean test(int num) {
        return num % 2 == 0;
    }

    @Name("haveTest")
    public static boolean haveTest(InputToCheckerArg<String> text) {
        return StringUtils.hasText(text.argValue());
    }


    @Test
    public void classTest() throws NoSuchMethodException {
        Method test = CheckerByLogicMethodTest.class.getDeclaredMethod("haveTest", InputToCheckerArg.class);
        ByLogicMethodChecker checker = new ByLogicMethodChecker(null, test);
        Assertions.assertEquals("haveTest", checker.name());
        CheckResult result = checker.check(null);
        Assertions.assertTrue(result.log().contains("java.lang.String"));

    }

    @Test
    public void publicTest() throws NoSuchMethodException {
        Method test = CheckerByLogicMethodTest.class.getDeclaredMethod("haveTest", InputToCheckerArg.class);
        ByLogicMethodChecker checker = new ByLogicMethodChecker(null, test);
        Assertions.assertEquals("haveTest", checker.name());
        CheckResult result = checker.check(null);
        Assertions.assertTrue(result.log().contains("java.lang.String"));

    }
}