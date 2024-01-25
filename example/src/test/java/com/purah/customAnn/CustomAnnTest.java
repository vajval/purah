package com.purah.customAnn;

import com.purah.ExampleApplication;
import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.exception.ArgCheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.purah.customAnn.pojo.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;

@SpringBootTest(classes = ExampleApplication.class)
public class CustomAnnTest {

    @Autowired
    CustomAnnService customAnnService;

    @Autowired
    PurahContext purahContext;

    /**
     * 下面的 properties也可以通过 配置文件来编写
     * - name: 所有字段自定义注解检测
     *    mapping:
     *      custom_ann:
     *         "[*]": 自定义注解检测
     */

    @BeforeEach
    public void beforeEach() {

        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("所有字段自定义注解检测");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("*", "自定义注解检测");
        properties.add("custom_ann", map);

        purahContext.regNewCombinatorialChecker(properties);
    }

    @Test
    public void test() {
        CustomUser customUser = new CustomUser();
        customUser.setId(50L);
        customUser.setName(null);
        Assertions.assertFalse(customAnnService.booleanCheck(customUser));
        Assertions.assertFalse(customAnnService.checkResult(customUser).isSuccess());

        ArgCheckException argCheckException = Assertions.assertThrows(ArgCheckException.class, () -> customAnnService.voidCheck(customUser));
        customUser.setId(1L);
        customUser.setName("abc");

        Assertions.assertTrue(customAnnService.booleanCheck(customUser));
        Assertions.assertTrue(customAnnService.checkResult(customUser).isSuccess());

        Assertions.assertDoesNotThrow(() -> customAnnService.voidCheck(customUser));


    }
}
