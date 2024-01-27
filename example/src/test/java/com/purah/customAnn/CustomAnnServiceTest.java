package com.purah.customAnn;

import com.purah.ExampleApplication;
import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.context.CheckerResult;
import com.purah.customAnn.pojo.CustomUser;
import com.purah.customSyntax.CustomSyntaxChecker;
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
class CustomAnnServiceTest {


    @Autowired
    CustomAnnService customAnnService;

    @Autowired
    PurahContext purahContext;

    /**
     * 下面的 properties也可以通过 配置文件来编写
     * - name: 所有字段自定义注解检测
     * mapping:
     * custom_ann:
     * "[*]": 自定义注解检测
     */


    CustomUser badCustomUser;
    CustomUser goodCustomUser;


    @BeforeEach
    public void beforeEach() {
        System.out.println("beforeEach");
        badCustomUser = new CustomUser(50L, null, "123", null);
        goodCustomUser = new CustomUser(3L, "vajva", "15509931234", 15);


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("所有字段自定义注解检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("*", "自定义注解检测");
        properties.addByStrMap("general", map);


        purahContext.regNewCombinatorialChecker(properties);

    }

    @Test
    void voidCheck() {


        ArgCheckException argCheckException = Assertions.assertThrows(ArgCheckException.class, () -> customAnnService.voidCheck(badCustomUser));

        Assertions.assertDoesNotThrow(() -> customAnnService.voidCheck(goodCustomUser));


    }

    @Test
    void checkResult() {
        CheckerResult goodCheckerResult = customAnnService.checkResult(goodCustomUser);
        Assertions.assertTrue(goodCheckerResult.isSuccess());

        assertEquals(5, ((List) goodCheckerResult.value()).size());
        CheckerResult badCheckerResult = customAnnService.checkResult(badCustomUser);
        Assertions.assertTrue(badCheckerResult.isFailed());

        assertEquals(1, ((List) badCheckerResult.value()).size());


    }

    @Test
    void booleanCheck() {

        Assertions.assertFalse(customAnnService.booleanCheck(badCustomUser));
        Assertions.assertTrue(customAnnService.booleanCheck(goodCustomUser));


    }

    @Test
    void booleanCheckDefaultCheckerByClassAnn() {
        Assertions.assertEquals(customAnnService.booleanCheck(goodCustomUser), customAnnService.booleanCheckDefaultCheckerByClassAnn(goodCustomUser));

        Assertions.assertEquals(customAnnService.booleanCheck(badCustomUser), customAnnService.booleanCheckDefaultCheckerByClassAnn(badCustomUser));


    }

    /**
     * example:[][i*:自定义注解检测]
     * 等价于
     * - name: 所有字段自定义注解检测
     * mapping:
     * general:
     * "[i*]": 自定义注解检测
     */


    @Autowired
    ApplicationContext applicationContext;

    @Test
    void booleanCheckByCustomSyntax() {

        Assertions.assertEquals(customAnnService.booleanCheck(goodCustomUser), customAnnService.booleanCheckByCustomSyntax(goodCustomUser));

        Assertions.assertEquals(customAnnService.booleanCheck(badCustomUser), customAnnService.booleanCheckByCustomSyntax(badCustomUser));

    }

    @Test
    void booleanCheckByCustomSyntaxWithMultiLevel2() {
        goodCustomUser.setChildCustomUser(badCustomUser);
        for (int i = 0; i < 100000; i++) {



            if (i % 10000 == 0) {
                System.out.println(i);
            }
            CheckerResult checkerResult = customAnnService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);
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

    @Test
    void booleanCheckByCustomSyntaxWithMultiLevel() {
        CheckerResult checkerResult = customAnnService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);

        Assertions.assertTrue(checkerResult.isSuccess());
        goodCustomUser.setChildCustomUser(badCustomUser);
        checkerResult = customAnnService.checkByCustomSyntaxWithMultiLevel(goodCustomUser);
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