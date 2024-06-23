package org.purah.core.checker.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.Checker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import org.purah.core.checker.result.CheckResult;

class BaseSupportCacheCheckerTest {
    static int count = 0;

    @BeforeEach
    void beforeEach() {
        count = 0;


    }

    Checker<Object, Object> checker = new AbstractBaseSupportCacheChecker<Object, Object>() {
        @Override
        public CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
            count++;
            return success(inputToCheckerArg, null);
        }
    };

    @Test
    void check() {
        checker.check(1);
        Assertions.assertEquals(count, 1);
        checker.check(1);
        Assertions.assertEquals(count, 2);
    }

    @Test
    void cache() {
        checker.check(1);
        Assertions.assertEquals(count, 1);

        PurahCheckInstanceCacheContext.execOnCacheContext(
                this::check5
        );

        Assertions.assertEquals(count, 2);
    }

    public void check5() {
        checker.check(1);
        checker.check(1);
        checker.check(1);
        checker.check(1);
        checker.check(1);

    }
}