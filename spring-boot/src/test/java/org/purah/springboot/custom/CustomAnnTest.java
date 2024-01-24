package org.purah.springboot.custom;

import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.exception.ArgCheckException;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.springboot.ExampleApplication;
import org.purah.springboot.custom.pojo.CustomUser;
import org.purah.springboot.custom.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

@SpringBootTest(classes = ExampleApplication.class)
public class CustomAnnTest {

    @Autowired
    TestService testService;

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
        Assertions.assertFalse(testService.booleanCheck(customUser));
        Assertions.assertFalse(testService.checkResult(customUser).isSuccess());

        ArgCheckException argCheckException = Assertions.assertThrows(ArgCheckException.class, () -> testService.voidCheck(customUser));
        customUser.setId(1L);
        customUser.setName("abc");

        Assertions.assertTrue(testService.booleanCheck(customUser));
        Assertions.assertTrue(testService.checkResult(customUser).isSuccess());

        Assertions.assertDoesNotThrow(() -> testService.voidCheck(customUser));


    }
}
