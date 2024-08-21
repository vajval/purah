package io.github.vajval.purah.spring.aop;

import com.google.common.collect.Sets;
import io.github.vajval.purah.core.checker.*;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.resolver.ClassReflectCache;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.ExampleApplication;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.spring.aop.exception.MethodArgCheckException;
import io.github.vajval.purah.spring.aop.result.ArgCheckResult;
import io.github.vajval.purah.spring.aop.result.MethodHandlerCheckResult;
import io.github.vajval.purah.util.Checkers;
import io.github.vajval.purah.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.*;
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
    public void aop2() {
        aop3(BAD_USER);
        Assertions.assertEquals(AspectTestService.value, 0);
        aop3(GOOD_USER);
        Assertions.assertEquals(AspectTestService.value, 1);
        aop3(BAD_USER);
        Assertions.assertEquals(AspectTestService.value, 1);

    }


    @Test
    public void customSyntax() {


        assertTrue(aspectTestService.customSyntax(GOOD_USER));
        assertFalse(aspectTestService.customSyntax(BAD_USER));
        assertFalse(aspectTestService.customSyntax(GOOD_USER_BAD_CHILD));
        assertTrue(aspectTestService.customSyntax(GOOD_USER_GOOD_CHILD));
        MultiCheckResult<?> multiCheckResult = aspectTestService.customSyntax(GOOD_USER_BAD_CHILD);
        List<LogicCheckResult<?>> logicCheckResults = multiCheckResult.resultChildList(ResultLevel.only_failed_only_base_logic);
        String collect = logicCheckResults.stream().map(LogicCheckResult::log).collect(Collectors.joining(","));
        Assertions.assertTrue(collect.contains("childUser.id:range wrong"));
        Assertions.assertTrue(collect.contains("childUser.name:this field cannot empty"));
        Assertions.assertTrue(collect.contains("childUser.phone:phone num wrong"));
//        Assertions.assertTrue(collect.contains("FAILED (field [childUser.age] type [java.lang.Integer])"));

    }

//    @Test
//
//    public void customSynt2sax() {
//
//
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
//        InputToCheckerArg<User> inputToCheckerArg = InputToCheckerArg.of(GOOD_USER_BAD_CHILD);
//        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
//        reflectArgResolver.configCache(true);
//        StopWatch stopWatch = new StopWatch("123");
//
//        int num=1_00_000;
////        int num = 1_000;
//        for (int i = 0; i < num; i++) {
//            reflectArgResolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
//        }
//
//        stopWatch.start("123");
//        for (int i = 0; i < num * 1000; i++) {
//            reflectArgResolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());
//    }


    static class W {
        ConcurrentHashMap<Class<?>, ClassReflectCache> classClassConfigCacheMap;

        public W() {
            classClassConfigCacheMap = new ConcurrentHashMap<>();
            HashSet<Class<?>> unSupportNestMatchClassSet = Sets.newHashSet(String.class, boolean.class, Boolean.class, int.class, Integer.class, short.class, Short.class, long.class, Long.class, byte.class, Byte.class, String.class, char.class, Character.class, Double.class, double.class, Float.class, float.class);
            for (Class<?> clazz : unSupportNestMatchClassSet) {
                classClassConfigCacheMap.put(clazz, ClassReflectCache.nullOrEmptyValueReflectCache);
            }
        }

        public int customSynt2wesax(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, ReflectArgResolver argResolver) {
            int w = 0;
            Class<?> inputArgClass = inputToCheckerArg.argClass();
            ClassReflectCache classReflectCache2 = classClassConfigCacheMap.computeIfAbsent(inputArgClass, ClassReflectCache::new);
            Object argValue = inputToCheckerArg.argValue();
            Map<String, InputToCheckerArg<?>> result = classReflectCache2.fullResultByInvokeCache(argValue, fieldMatcher);
            if (result != null) {
                return 0;
            }
            result = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
            classReflectCache2.tryRegNewInvokeCache(inputToCheckerArg, fieldMatcher, result);
            return w;
        }
    }


//    @Test
//    public void customSynt2sasadx() {
//
//        ComboBuilderChecker customAnnCheck = purahs.combo()
//                .match(new GeneralFieldMatcher("*"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.id"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.name"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.phone|childUser.age"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.age"), "custom_ann_check")
//                .mainMode(ExecMode.Main.all_success_but_must_check_all);
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start("Generics");
//        for (int i = 0; i < 1 * 1000 * 1000; i++) {
//            customAnnCheck.oCheck(GOOD_USER_BAD_CHILD);
//        }
//        stopWatch.stop();
//        customAnnCheck = purahs.combo()
//                .match(new GeneralFieldMatcher("*"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.id"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.name"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.phone|childUser.age"), "custom_ann_check")
//                .match(new GeneralFieldMatcher("childUser.age"), "custom_ann_check")
//                .mainMode(ExecMode.Main.all_success).autoReOrder(100);
//        stopWatch.start("zdxfv");
//        for (int i = 0; i < 1 * 1000 * 1000; i++) {
//            customAnnCheck.oCheck(GOOD_USER_BAD_CHILD);
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());
//    }
//
//    @Test
//    public void customSynt2asdax() {
//        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
//
//        MyCustomAnnChecker myCustomAnnChecker = new MyCustomAnnChecker();
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
//
//        for (int i = 0; i < 1 * 1_000_000*10; i++) {
//            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.oGetMatchFieldObjectMap(GOOD_USER_BAD_CHILD, generalFieldMatcher);
//
//        }
//    }
}
//
//    }

//
//    @Test
//    public void customSynt2ax() {
//        StopWatch stopWatch = new StopWatch();
//        MyCustomAnnChecker myCustomAnnChecker = new MyCustomAnnChecker();
//
//        ArgResolver argResolver = purahs.argResolver();
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher("*|childUser.id|childUser.name|childUser.phone|childUser.age");
//
//
//        stopWatch.start("Generics");
//        GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createByChecker(myCustomAnnChecker);
//        for (int i = 0; i < 1 * 100 * 1000; i++) {
//            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.oGetMatchFieldObjectMap(GOOD_USER_BAD_CHILD, generalFieldMatcher);
//            for (Map.Entry<String, InputToCheckerArg<?>> stringInputToCheckerArgEntry : matchFieldObjectMap.entrySet()) {
//                genericsProxyChecker.check((InputToCheckerArg) stringInputToCheckerArgEntry.getValue());
//            }
//
//        }
//        stopWatch.stop();
//
//        stopWatch.start("myCustomAnnChecker");
//        for (int i = 0; i < 1 * 100 * 1000; i++) {
//            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.oGetMatchFieldObjectMap(GOOD_USER_BAD_CHILD, generalFieldMatcher);
//
//            for (Map.Entry<String, InputToCheckerArg<?>> stringInputToCheckerArgEntry : matchFieldObjectMap.entrySet()) {
//                myCustomAnnChecker.check((InputToCheckerArg) stringInputToCheckerArgEntry.getValue());
//            }
//        }
//        stopWatch.stop();
//
//        stopWatch.start("purah");
//        ComboBuilderChecker customAnnCheck = purahs.combo().match(generalFieldMatcher, "custom_ann_check").mainMode(ExecMode.Main.all_success_but_must_check_all);
//
//        for (int i = 0; i < 1 * 100 * 1000; i++) {
//            customAnnCheck.oCheck(GOOD_USER_BAD_CHILD);
//        }
//        stopWatch.stop();
//        stopWatch.start("genericsProxyChecker");
//        genericsProxyChecker = GenericsProxyChecker.createByChecker(myCustomAnnChecker);
//        for (int i = 0; i < 1 * 100 * 1000; i++) {
//            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.oGetMatchFieldObjectMap(GOOD_USER_BAD_CHILD, generalFieldMatcher);
//            for (Map.Entry<String, InputToCheckerArg<?>> stringInputToCheckerArgEntry : matchFieldObjectMap.entrySet()) {
//                genericsProxyChecker.check((InputToCheckerArg) stringInputToCheckerArgEntry.getValue());
//            }
//
//        }
//        stopWatch.stop();
//        stopWatch.start("purah2");
//        for (int i = 0; i < 1 * 100 * 1000; i++) {
//            customAnnCheck.oCheck(GOOD_USER_BAD_CHILD);
//        }
//        stopWatch.stop();
//
//        System.out.println(stopWatch.prettyPrint());
//
//
//    }

//}
