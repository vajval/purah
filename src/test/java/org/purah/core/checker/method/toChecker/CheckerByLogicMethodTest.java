package org.purah.core.checker.method.toChecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.base.Name;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.result.CheckResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class CheckerByLogicMethodTest {


    @Name("numTest")
    protected static boolean test(int num) {
        return num % 2 == 0;
    }

    @Name("haveTest")
    public static boolean haveTest(InputCheckArg<String> text) {
        return StringUtils.hasText(text.inputArg());
    }


    @Test
    public void classTest() throws NoSuchMethodException {
        Method test = CheckerByLogicMethodTest.class.getDeclaredMethod("haveTest", InputCheckArg.class);
        CheckerByLogicMethod checker = new CheckerByLogicMethod(null, test);
        Assertions.assertEquals("haveTest", checker.name());
        CheckResult result = checker.check(null);
        Assertions.assertTrue(result.log().contains("java.lang.String"));

    }

    @Test
    public void publicTest() throws NoSuchMethodException {
        Method test = CheckerByLogicMethodTest.class.getDeclaredMethod("haveTest", InputCheckArg.class);
        CheckerByLogicMethod checker = new CheckerByLogicMethod(null, test);
        Assertions.assertEquals("haveTest", checker.name());
        CheckResult result = checker.check(null);
        Assertions.assertTrue(result.log().contains("java.lang.String"));

    }
}