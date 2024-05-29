package org.purah.springboot.matcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.purah.core.PurahContext;
import org.purah.core.matcher.factory.MatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ExampleApplication.class)
class PurahContextFactoryBeanTest {

    @Autowired
    PurahContext purahContext;

    @Test
    void initMatcherManager() {
        MatcherFactory justTest = purahContext.matcherManager().factoryOf("just_test");
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