package io.github.vajval.purah.core.checker;

import io.github.vajval.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BaseSupportCacheCheckerTest {
    static int count = 0;

    @BeforeEach
    void beforeEach() {
        count = 0;
    }

    final Checker<Object, Object> checker = new AbstractBaseSupportCacheChecker<Object, Object>() {
        @Override
        public CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
            count++;
            return LogicCheckResult.success(inputToCheckerArg, null);
        }
    };

    @Test
    void check() {
        checker.oCheck(1);
        Assertions.assertEquals(count, 1);
        checker.oCheck(1);
        Assertions.assertEquals(count, 2);
    }

    @Test
    void cache() {
        checker.oCheck(1);
        Assertions.assertEquals(count, 1);

        PurahCheckInstanceCacheContext.execOnCacheContext(
                this::check5
        );

        Assertions.assertEquals(count, 2);
    }

    public void check5() {
        checker.oCheck(1);
        checker.oCheck(1);
        checker.oCheck(1);
        checker.oCheck(1);
        checker.oCheck(1);

    }
}