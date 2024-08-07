package io.github.vajval.purah.core.checker.converter.checker;

import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.ComboBuilderChecker;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.util.People;
import io.github.vajval.purah.util.TestAnn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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


    Purahs purahs;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException {
        purahs = new Purahs(new PurahContext());
        Method method = ByAnnMethodCheckerTest.class.getMethod("testByName", TestAnn.class, String.class);
        ByAnnMethodChecker byAnnMethodChecker = new ByAnnMethodChecker(null, method, "test",AutoNull.notEnable);
        purahs.reg(byAnnMethodChecker);

    }


    @Test
    void test() {

        ComboBuilderChecker checker = purahs.combo().match(new GeneralFieldMatcher("name"), "test");

        CheckResult<?> result = checker.oCheck(People.elder);
        Assertions.assertTrue(result);

        checker = purahs.combo("test");
        result = checker.oCheck("123");
        Assertions.assertFalse(result);//no ann

        checker = purahs.combo().match(new GeneralFieldMatcher("child#0.name"), "test");


        result = checker.oCheck(People.elder);
        Assertions.assertTrue(result);


        checker = purahs.combo().match(new GeneralFieldMatcher("child#0.id"), "test");
        result = checker.oCheck(People.elder);
        Assertions.assertFalse(result);  //no ann

        checker = purahs.combo().match(new GeneralFieldMatcher("child#0.child#0.child#0.id"), "test");
        result = checker.oCheck(People.elder);
        Assertions.assertFalse(result);  //no child

    }

}