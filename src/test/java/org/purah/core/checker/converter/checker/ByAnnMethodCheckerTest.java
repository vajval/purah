package org.purah.core.checker.converter.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.Purahs;
import org.purah.core.name.Name;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.matcher.nested.GeneralFieldMatcher;
import org.purah.util.People;
import org.purah.util.TestAnn;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class ByAnnMethodCheckerTest {

    @Name("test")
    public static boolean testByName(TestAnn testAnn, String str) {
        if (!StringUtils.hasText(str)) {
            return false;
        }
        if (testAnn == null) {
            return false;
        }
        return StringUtils.hasText(testAnn.value());
    }


    Purahs purahs; ;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException {
        purahs = new Purahs(new PurahContext());
        Method method = ByAnnMethodCheckerTest.class.getMethod("testByName", TestAnn.class, String.class);
        ByAnnMethodChecker byAnnMethodChecker = new ByAnnMethodChecker(null, method, "test");
        purahs.reg(byAnnMethodChecker);

    }


    @Test
    void test() {

        ComboBuilderChecker checker = purahs.combo().match(new GeneralFieldMatcher("name"), "test");

        CheckResult<?> result = checker.check(People.elder);
        Assertions.assertTrue(result);

        checker = purahs.combo("test");
        result = checker.check("123");
        Assertions.assertFalse(result);//no ann

        checker = purahs.combo().match(new GeneralFieldMatcher("child#0.name"), "test");


        result = checker.check(People.elder);
        Assertions.assertTrue(result);


        checker = purahs.combo().match(new GeneralFieldMatcher("child#0.id"), "test");
        result = checker.check(People.elder);
        Assertions.assertFalse(result);  //no ann

        checker = purahs.combo().match(new GeneralFieldMatcher("child#0.child#0.child#0.id"), "test");
        result = checker.check(People.elder);
        Assertions.assertFalse(result);  //no child

    }

}