package org.purah.example.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.purah.core.PurahContext;
import org.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.exception.MethodArgCheckException;
import org.purah.ExampleApplication;
import org.purah.example.customAnn.CustomService;
import org.purah.example.customAnn.ann.CNPhoneNum;
import org.purah.example.customAnn.ann.NotEmpty;
import org.purah.example.customAnn.ann.NotNull;
import org.purah.example.customAnn.checker.CustomAnnChecker;
import org.purah.example.customAnn.pojo.CustomUser;
import org.purah.springboot.result.AutoFillCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExampleApplication.class)
class CustomServiceTest {


    @Autowired
    CustomService customService;

    @Autowired
    PurahContext purahContext;

    /*
     * 下面的 properties也可以通过 配置文件来编写
     *  - name: 所有字段自定义注解检测
     *      mapping:
     *         general:
     *          "[*]": 自定义注解检测
     */


    CustomUser badCustomUser;
    CustomUser goodCustomUser;


    @BeforeEach
    public void beforeEach() {
        badCustomUser = new CustomUser(50L, null, "123", null);
        goodCustomUser = new CustomUser(3L, "vajva", "15509931234", 15);


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("所有字段自定义注解检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("*", "自定义注解检测");
        properties.addByStrMap("general", map);
        properties.setLogicFrom("CustomServiceTest.beforeEach");


        purahContext.regNewCombinatorialChecker(properties);

    }

    @Test
    void voidCheck() {


        MethodArgCheckException methodArgCheckException = Assertions.assertThrows(MethodArgCheckException.class, () -> customService.voidCheck(badCustomUser));

        Assertions.assertDoesNotThrow(() -> customService.voidCheck(goodCustomUser));


    }

    @Test
    void checkResult() {
        CheckResult goodCheckResult = customService.checkResult(goodCustomUser);

        assertTrue(goodCheckResult.isSuccess());

//        assertEquals(4, ((List) goodCheckResult.value()).size());
        CheckResult badCheckResult = customService.checkResult(badCustomUser);

        assertTrue(badCheckResult.isFailed());

        assertEquals(1, ((List) badCheckResult.value()).size());


    }

    @Test
    void booleanCheck() {

        assertFalse(customService.booleanCheck(badCustomUser));
        assertTrue(customService.booleanCheck(goodCustomUser));


    }

    @Test
    void booleanCheckDefaultCheckerByClassAnn() {
        System.out.println(customService.booleanCheck(null));
        Assertions.assertEquals(customService.booleanCheck(null), customService.booleanCheckDefaultCheckerByClassAnn(goodCustomUser));

        Assertions.assertEquals(customService.booleanCheck(badCustomUser), customService.booleanCheckDefaultCheckerByClassAnn(badCustomUser));


    }

    /*
     * example:0[][i*:自定义注解检测]
     *  等价于
     *  - name: 所有字段自定义注解检测
     *      mapping:
     *        general:
     *           "[i*]": 自定义注解检测
     */


    @Autowired
    ApplicationContext applicationContext;

    @Test
    void booleanCheckByCustomSyntax() {

        Assertions.assertEquals(customService.booleanCheck(goodCustomUser), customService.booleanCheckByCustomSyntax(goodCustomUser));

        Assertions.assertEquals(customService.booleanCheck(badCustomUser), customService.booleanCheckByCustomSyntax(badCustomUser));

    }


    public Long id;
    @NotEmpty(errorMsg = "这个字段不能为空")
    public String name;
    @CNPhoneNum(errorMsg = "移不动也联不通")
    public String phone;


    @NotNull(errorMsg = "norBull")
    public Integer age;

    CustomUser childCustomUser;

    @Test
    void booleanCheckByCustomSyntaxWithMultiLevel() {
//        CheckResult CheckResult = customService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);

//        assertTrue(CheckResult.isSuccess());
        goodCustomUser.setChildCustomUser(badCustomUser);
        System.out.println("------------------------------------");
        AutoFillCheckResult checkResult = customService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);
        assertFalse(checkResult.isSuccess());
        List<BaseLogicCheckResult> resultList = checkResult.allBaseLogicCheckResult(ResultLevel.failedAndIgnoreNotBaseLogic);
//        value.stream().
        String trim = resultList.stream().map(CheckResult::log)
                .reduce("", (a, b) -> a + b).trim();
        /*
         * 检测
         * id
         * name
         * phone
         * age
         * childCustomUser
         *
         * childCustomUser.id
         * childCustomUser.name
         * childCustomUser.phone
         * childCustomUser.age
         * childCustomUser.childCustomUser
         *
         */
//        System.out.println(((MethodCheckResult) checkResult).mainCheckResult());


//
//        MethodCheckResult methodCheckResult = (AutoFillCheckResult) checkResult;
//        Assertions.assertTrue(methodCheckResult.isFailed());
//        ArgCheckResult arg0CheckResult = methodCheckResult.argResultOf(0);
//        Assertions.assertTrue(arg0CheckResult.isFailed());
//
//        Assertions.assertEquals(arg0CheckResult.value().size(), 5);
//
//        Assertions.assertTrue(methodCheckResult.isFailed());
//
//
//        Assertions.assertEquals(resultList.size(), 5);
//
//
//        assertTrue(trim.contains("childCustomUser.id:取值范围错误"));
//        assertTrue(trim.contains("childCustomUser.name:这个字段不能为空"));
//        assertTrue(trim.contains("childCustomUser.phone:移不动也联不通"));
//        for (CheckResult result : resultList) {
//            System.out.println(result.isSuccess());
//            System.out.println(result.log());
//            System.out.println(result.checkLogicFrom());
//        }
//
//        System.out.println();

    }


    @Test
    void multiLevel() {

    }

    @Test
    void booleanCheckMultiArgs() {

        assertTrue(customService.booleanCheck(goodCustomUser, goodCustomUser, goodCustomUser));

        assertFalse(customService.booleanCheck(goodCustomUser, goodCustomUser, badCustomUser));

        assertTrue(customService.booleanCheck(goodCustomUser, badCustomUser, goodCustomUser));

        assertFalse(customService.booleanCheck(goodCustomUser, badCustomUser, badCustomUser));


        assertFalse(customService.booleanCheck(badCustomUser, goodCustomUser, goodCustomUser));

        assertFalse(customService.booleanCheck(badCustomUser, goodCustomUser, badCustomUser));

        assertFalse(customService.booleanCheck(badCustomUser, badCustomUser, goodCustomUser));

        assertFalse(customService.booleanCheck(badCustomUser, badCustomUser, badCustomUser));

//

    }


    @Test
    void booleanCheckByCustomSyntaxWithMultiLevel2() {
        int num = 100;
        test(num);
        assertEquals(2 * num, CustomAnnChecker.cnPhoneNumCount);

        PurahCheckInstanceCacheContext.createEnableOnThread();
        test(num);
        assertEquals(2, CustomAnnChecker.cnPhoneNumCount);
        PurahCheckInstanceCacheContext.closeCache();

        test(num);
        assertEquals(2 * num, CustomAnnChecker.cnPhoneNumCount);


        PurahCheckInstanceCacheContext.createEnableOnThread();
        PurahCheckInstanceCacheContext.createEnableOnThread();

        test(num);
        assertEquals(2, CustomAnnChecker.cnPhoneNumCount);
        PurahCheckInstanceCacheContext.closeCache();

        test(num);
        assertEquals(2, CustomAnnChecker.cnPhoneNumCount);
        PurahCheckInstanceCacheContext.closeCache();




        test(num);
        assertEquals(2 * num, CustomAnnChecker.cnPhoneNumCount);

    }

    public void test(int num) {
        CustomAnnChecker.cnPhoneNumCount = 0;


        goodCustomUser.setChildCustomUser(badCustomUser);
        for (int i = 0; i < num; i++) {
            AutoFillCheckResult autoFillCheckResult = customService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);
            assertFalse(autoFillCheckResult.isSuccess());


            String trim = autoFillCheckResult.allBaseLogicCheckResult().stream().filter(w -> w.isFailed()).map(w -> w.log())
                    .reduce("", (a, b) -> a + b).trim();

            assertTrue(trim.contains("childCustomUser.id:取值范围错误"));
            assertTrue(trim.contains("childCustomUser.name:这个字段不能为空"));
            assertTrue(trim.contains("childCustomUser.phone:移不动也联不通"));
        }
    }
}