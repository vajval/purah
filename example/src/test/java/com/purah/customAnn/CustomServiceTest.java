package com.purah.customAnn;

import com.purah.ExampleApplication;
import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.context.CheckerResult;
import com.purah.customAnn.ann.CNPhoneNum;
import com.purah.customAnn.ann.NotEmpty;
import com.purah.customAnn.ann.NotNull;
import com.purah.customAnn.ann.Range;
import com.purah.customAnn.pojo.CustomUser;
import com.purah.exception.ArgCheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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


        ArgCheckException argCheckException = Assertions.assertThrows(ArgCheckException.class, () -> customService.voidCheck(badCustomUser));

        Assertions.assertDoesNotThrow(() -> customService.voidCheck(goodCustomUser));


    }

    @Test
    void checkResult() {
        CheckerResult goodCheckerResult = customService.checkResult(goodCustomUser);

        Assertions.assertTrue(goodCheckerResult.isSuccess());

//        assertEquals(4, ((List) goodCheckerResult.value()).size());
        CheckerResult badCheckerResult = customService.checkResult(badCustomUser);

        Assertions.assertTrue(badCheckerResult.isFailed());

        assertEquals(1, ((List) badCheckerResult.value()).size());


    }

    @Test
    void booleanCheck() {

        Assertions.assertFalse(customService.booleanCheck(badCustomUser));
        Assertions.assertTrue(customService.booleanCheck(goodCustomUser));


    }

    @Test
    void booleanCheckDefaultCheckerByClassAnn() {
        Assertions.assertEquals(customService.booleanCheck(goodCustomUser), customService.booleanCheckDefaultCheckerByClassAnn(goodCustomUser));

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

            @Test
    void booleanCheckByCustomSyntaxWithMultiLevel2() {
        goodCustomUser.setChildCustomUser(badCustomUser);
        for (int i = 0; i < 10000000; i++) {


            if (i % 10000 == 0) {
                System.out.println(i);
            }
            CheckerResult checkerResult = customService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);
            Assertions.assertFalse(checkerResult.isSuccess());
            List<CheckerResult> resultList = (List) checkerResult.value();
//        value.stream().
            String trim = resultList.stream().filter(CheckerResult::isFailed).map(CheckerResult::info)
                    .reduce("", (a, b) -> a + b).trim();

            Assertions.assertTrue(trim.contains("childCustomUser.id:取值范围错误"));
            Assertions.assertTrue(trim.contains("childCustomUser.name:这个字段不能为空"));
            Assertions.assertTrue(trim.contains("childCustomUser.phone:移不动也联不通"));
        }
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
        CheckerResult checkerResult = customService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);

        Assertions.assertTrue(checkerResult.isSuccess());
        goodCustomUser.setChildCustomUser(badCustomUser);
        System.out.println("------------------------------------");
        checkerResult = customService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);
        Assertions.assertFalse(checkerResult.isSuccess());
        List<CheckerResult> resultList = (List) checkerResult.value();
//        value.stream().
        String trim = resultList.stream().filter(CheckerResult::isFailed).map(CheckerResult::info)
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



        Assertions.assertEquals(resultList.size(),8);


        Assertions.assertTrue(trim.contains("childCustomUser.id:取值范围错误"));
        Assertions.assertTrue(trim.contains("childCustomUser.name:这个字段不能为空"));
        Assertions.assertTrue(trim.contains("childCustomUser.phone:移不动也联不通"));
        for (CheckerResult result : resultList) {
            System.out.println(result.isSuccess());
            System.out.println(result.info());
            System.out.println(result.logicFrom());
        }

        System.out.println();

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


    }

}