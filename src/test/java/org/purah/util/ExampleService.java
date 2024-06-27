package org.purah.util;

import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.purah.core.PurahContext;
import org.purah.core.checker.Checker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ExampleApplication.class)

public class ExampleService {
    @Autowired
    PurahContext purahContext;

    @Test
    public void regPeople() {

        People people = People.of("长者");
        Checker<Object, Object> checker = purahContext.combo("用户检测");
        System.out.println(People.mapOf("长者"));
        Object value = checker.check(null).data();
        System.out.println(value);
         value = checker.check(People.of("不知道在哪的孩子")).data();
        System.out.println(value);

        value = checker.check(People.of("孙子")).data();
        System.out.println(value);
        value = checker.check(People.of("长者")).data();
        System.out.println(value);
        value = checker.check(People.mapOf("长者")).data();
        System.out.println(value);
    }
}
