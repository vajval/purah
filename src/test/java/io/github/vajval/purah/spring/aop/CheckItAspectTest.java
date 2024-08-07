package io.github.vajval.purah.spring.aop;

import io.github.vajval.purah.core.checker.ComboBuilderChecker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.MyCustomAnnChecker;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.resolver.ArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.ExampleApplication;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.GenericsProxyChecker;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.spring.aop.exception.MethodArgCheckException;
import io.github.vajval.purah.spring.aop.result.ArgCheckResult;
import io.github.vajval.purah.spring.aop.result.MethodHandlerCheckResult;
import io.github.vajval.purah.util.Checkers;
import io.github.vajval.purah.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.github.vajval.purah.util.User.*;

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

//    @Test
//    public void customSynt2ax() {
//        StopWatch stopWatch=new StopWatch();
//        MyCustomAnnChecker myCustomAnnChecker = new MyCustomAnnChecker();
//
//        ArgResolver argResolver = purahs.argResolver();
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
//        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(GOOD_USER_BAD_CHILD, generalFieldMatcher);
//
//        stopWatch.start("1");
//        for (int i = 0; i < 2 * 1000 * 1000; i++) {
//            for (Map.Entry<String, InputToCheckerArg<?>> stringInputToCheckerArgEntry : matchFieldObjectMap.entrySet()) {
//                myCustomAnnChecker.check((InputToCheckerArg)stringInputToCheckerArgEntry.getValue());
//            }
//        }
//        stopWatch.stop();
//        stopWatch.start("2");
//        GenericsProxyChecker genericsProxyChecker =GenericsProxyChecker.createByChecker(myCustomAnnChecker);
//        for (int i = 0; i < 2 * 1000 * 1000; i++) {
//            for (Map.Entry<String, InputToCheckerArg<?>> stringInputToCheckerArgEntry : matchFieldObjectMap.entrySet()) {
//                genericsProxyChecker.check((InputToCheckerArg)stringInputToCheckerArgEntry.getValue());
//            }
//
//        }
//        stopWatch.stop();
//        stopWatch.start("3");
//        ComboBuilderChecker customAnnCheck = purahs.combo().match(generalFieldMatcher, "custom_ann_check");
//        for (int i = 0; i < 2 * 1000 * 1000; i++) {
//            customAnnCheck.oCheck(GOOD_USER_BAD_CHILD);
//        }
//        stopWatch.stop();
//        stopWatch.start("4");
//         genericsProxyChecker =GenericsProxyChecker.createByChecker(myCustomAnnChecker);
//        for (int i = 0; i < 2 * 1000 * 1000; i++) {
//            for (Map.Entry<String, InputToCheckerArg<?>> stringInputToCheckerArgEntry : matchFieldObjectMap.entrySet()) {
//                genericsProxyChecker.check((InputToCheckerArg)stringInputToCheckerArgEntry.getValue());
//            }
//
//        }
//        stopWatch.stop();
//
//
//        String s = stopWatch.prettyPrint();
//        System.out.println(s);
//
//
//    }

}
