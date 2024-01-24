package org.purah.springboot.core;

import com.purah.PurahContext;
import com.purah.matcher.factory.MatcherFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.springboot.ExampleApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExampleApplication.class)
class PurahContextFactoryBeanTest {

    @Autowired
    PurahContext purahContext;

    @Test
    void initMatcherManager() {
        Assertions.assertDoesNotThrow(
                () -> purahContext.matcherManager().factoryOf("just_test")

        );
        Assertions.assertThrows(Exception.class,
                () -> purahContext.matcherManager().factoryOf("just_test2")
        );
    }

    @Test
    void initArgResolverManager() {
    }

    @Test
    void initCheckerManager() {
    }

    @Test
    void initPurahConfigProperties() {
    }
}