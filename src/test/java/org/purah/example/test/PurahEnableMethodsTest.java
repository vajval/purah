package org.purah.example.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.purah.ExampleApplication;
import org.purah.core.PurahContext;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.CheckResult;
import org.purah.example.customAnn.pojo.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;

@SpringBootTest(classes = ExampleApplication.class)
class PurahEnableMethodsTest {
    /**
     * 在这里
     */
    @Autowired
    MethodsToCheckersTestBean
            methodsToCheckersTestBean;
    @Autowired
    PurahContext purahContext;


    @Test
    public void test() {
        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties("测试MethodsToCheckers注解");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("name", "非空判断FromTestBean,有文本判断FromTestBean");

        map.put("age", "1取值必须在[1-15]之间判断FromTestBean,2取值必须在[5-20]之间判断FromTestBean,3取值必须在[3-7]之间判断FromTestBean");

        map.put("id", "数值判断FromTestBean");
        properties.addByStrMap("general", map);
        properties.setMainExecType(ExecMode.Main.all_success_but_must_check_all);
        purahContext.createAndRegByProperties(properties);


        CustomUser badCustomUser = new CustomUser(-1L, null, null, 35);

        GenericsProxyChecker genericsProxyChecker = purahContext.checkManager().of("测试MethodsToCheckers注解");
        CheckResult check = genericsProxyChecker.check(badCustomUser);




        CheckResult checkResult = genericsProxyChecker.check(badCustomUser);
        Assertions.assertFalse(checkResult.isError());


    }

}
