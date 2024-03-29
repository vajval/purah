package com.purah.customAnn;

import com.purah.ExampleApplication;
import com.purah.PurahContext;
import com.purah.base.Name;
import com.purah.checker.CheckInstance;
import com.purah.checker.ExecChecker;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.combinatorial.ExecType;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.customAnn.pojo.CustomUser;
import com.purah.springboot.config.PurahConfigProperties;
import org.junit.jupiter.api.Test;
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
        properties.setMainExecType(ExecType.Main.all_success_but_must_check_all);
        purahContext.regNewCombinatorialChecker(properties);


        CustomUser badCustomUser = new CustomUser(-1L, null, null, 35);

        ExecChecker execChecker = purahContext.checkManager().get("测试MethodsToCheckers注解");
        CheckerResult check = execChecker.check(CheckInstance.create(badCustomUser));
        System.out.println(check);
        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult) execChecker.check(CheckInstance.create(badCustomUser));
        System.out.println(checkerResult.logicFrom());
        for (CheckerResult result : checkerResult.value()) {
            System.out.println(result);
            System.out.println(result.logicFrom());
        }

    }

}
