package com.purah.customAnn;

import com.purah.ExampleApplication;
import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.context.CheckerResult;
import com.purah.customAnn.pojo.CustomUser;
import com.purah.exception.ArgCheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
     *      mapping:
     *         custom_ann:
     *            "[*]": 自定义注解检测
     */


    CustomUser badCustomUser = new CustomUser(50L, null, "123");
    CustomUser goodCustomUser = new CustomUser(3L, "vajva", "15509931234");


    @BeforeEach
    public void beforeEach() {

        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("所有字段自定义注解检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("*", "自定义注解检测");
        properties.add("custom_ann", map);


        purahContext.regNewCombinatorialChecker(properties);

    }

    @Test
    void voidCheck() {


        ArgCheckException argCheckException = Assertions.assertThrows(ArgCheckException.class, () -> customAnnService.voidCheck(badCustomUser));
        System.out.println(argCheckException);

        Assertions.assertDoesNotThrow(() -> customAnnService.voidCheck(goodCustomUser));


    }

    @Test
    void checkResult() {
        CheckerResult goodCheckerResult = customAnnService.checkResult(goodCustomUser);
        Assertions.assertTrue(goodCheckerResult.isSuccess());

        assertEquals(3, ((List) goodCheckerResult).size());
        CheckerResult badCheckerResult = customAnnService.checkResult(badCustomUser);
        Assertions.assertTrue(goodCheckerResult.isFailed());

        assertEquals(1, ((List) badCheckerResult).size());


    }

    @Test

    void booleanCheck() {

        Assertions.assertFalse(customAnnService.booleanCheck(badCustomUser));
        Assertions.assertTrue(customAnnService.booleanCheck(goodCustomUser));


    }
    @Test
    void  booleanCheckDefaultCheckerByClassAnn(){
        Assertions.assertEquals(customAnnService.booleanCheck(goodCustomUser),customAnnService.booleanCheckDefaultCheckerByClassAnn(goodCustomUser));

        Assertions.assertEquals(customAnnService.booleanCheck(badCustomUser),customAnnService.booleanCheckDefaultCheckerByClassAnn(badCustomUser));
    }
}