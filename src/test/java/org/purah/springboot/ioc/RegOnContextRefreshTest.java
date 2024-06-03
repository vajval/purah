package org.purah.springboot.ioc;

import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExampleApplication.class)
class RegOnContextRefreshTest {
    @Autowired
    RegOnContextRefresh regOnContextRefresh;
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void onApplicationEvent() {
        Set<Object> objects = regOnContextRefresh.purahEnableMethodsBean(applicationContext);
        for (Object object : objects) {
            System.out.println(object);
        }
    }

    @Test
    void initMatcherManager() {
    }

    @Test
    void regChecker() {
    }
}