package org.purah.springboot.aop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.purah.core.Purahs;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.result.LogicCheckResult;
import org.purah.core.checker.result.MultiCheckResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.nested.GeneralFieldMatcher;
import org.purah.springboot.aop.exception.MethodArgCheckException;
import org.purah.springboot.aop.result.ArgCheckResult;
import org.purah.springboot.aop.result.MethodHandlerCheckResult;
import org.purah.util.Checkers;
import org.purah.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.purah.util.User.*;

@SpringBootTest(classes = ExampleApplication.class)
public class CheckItAspectTest {

    @Autowired
    AspectTestService aspectTestService;
    @Autowired
    Purahs purahs;

    GenericsProxyChecker allFieldCustomAnnChecker;

    @BeforeEach
    public void beforeEach() {

        if (allFieldCustomAnnChecker == null) {
            allFieldCustomAnnChecker = purahs.combo().match(new GeneralFieldMatcher("*"), Checkers.Name.CUSTOM_ANN_CHECK).regSelf("all_field_custom_ann_check");
            purahs.combo().match(new GeneralFieldMatcher("*|*.*"), Checkers.Name.CUSTOM_ANN_CHECK).regSelf("all_field_and_child_all_field_custom_ann_check");

        }

    }

    public void aop3(User user) {
        try {
            aspectTestService.checkOneUserThrow(user);
        } catch (MethodArgCheckException methodArgCheckException) {
            MethodHandlerCheckResult methodHandlerCheckResult = methodArgCheckException.checkResult();
            Assertions.assertFalse(methodHandlerCheckResult.argResultOf(0));
            for (ArgCheckResult argCheckResult : methodHandlerCheckResult.argCheckResultList()) {
                System.out.println(argCheckResult.failedLogicList());
            }

        }
    }

    @Test
    public void aop2() {
        aop3(BAD_USER);
        Assertions.assertEquals(AspectTestService.value, 0);

        aop3(GOOD_USER);
        Assertions.assertEquals(AspectTestService.value, 1);
        aop3(BAD_USER);
        Assertions.assertEquals(AspectTestService.value, 1);

    }

    @Test
    public void aop() {
        assertTrue(aspectTestService.checkThreeUser(GOOD_USER, GOOD_USER, GOOD_USER));
        assertFalse(aspectTestService.checkThreeUser(GOOD_USER, GOOD_USER, BAD_USER));
        assertTrue(aspectTestService.checkThreeUser(GOOD_USER, BAD_USER, GOOD_USER));
        assertFalse(aspectTestService.checkThreeUser(GOOD_USER, BAD_USER, BAD_USER));
        assertFalse(aspectTestService.checkThreeUser(BAD_USER, GOOD_USER, GOOD_USER));
        assertFalse(aspectTestService.checkThreeUser(BAD_USER, GOOD_USER, BAD_USER));
        assertFalse(aspectTestService.checkThreeUser(BAD_USER, BAD_USER, GOOD_USER));
        assertFalse(aspectTestService.checkThreeUser(BAD_USER, BAD_USER, GOOD_USER));

    }


    @Test
    public void customSyntax() {


        assertTrue(aspectTestService.customSyntax(GOOD_USER_GOOD_CHILD));
        assertTrue(aspectTestService.customSyntax(GOOD_USER));
        assertFalse(aspectTestService.customSyntax(BAD_USER));
        assertFalse(aspectTestService.customSyntax(GOOD_USER_BAD_CHILD));

        MultiCheckResult<?> multiCheckResult = aspectTestService.customSyntax(GOOD_USER_BAD_CHILD);
        List<LogicCheckResult<?>> logicCheckResults = multiCheckResult.resultChildList(ResultLevel.only_failed_only_base_logic);
        String collect = logicCheckResults.stream().map(LogicCheckResult::log).collect(Collectors.joining(","));


        Assertions.assertTrue(collect.contains("childUser.id:range wrong"));
        Assertions.assertTrue(collect.contains("childUser.name:this field cannot empty"));
        Assertions.assertTrue(collect.contains("childUser.phone:phone num wrong"));
        Assertions.assertTrue(collect.contains("FAILED (field [childUser.age] type [java.lang.Integer])"));

    }

}
