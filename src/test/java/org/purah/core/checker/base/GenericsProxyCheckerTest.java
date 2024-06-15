package org.purah.core.checker.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckerException;

import static org.junit.jupiter.api.Assertions.*;
import static org.purah.core.Util.*;

public class GenericsProxyCheckerTest {
    public static Checker<Util.Trade, Object> tradeChecker =
            Checkers.autoStringChecker("id1", i -> i.getInitiator().getId().equals(1L), Util.Trade.class);

    public static Checker<Util.User, Object> userChecker =
            Checkers.autoStringChecker("id1", i -> i.getId().equals(1L), Util.User.class);


    @Test
    void get() {
        GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createByChecker(tradeChecker);

        CheckResult<Object> result = genericsProxyChecker.check(trade);
        Assertions.assertEquals(result.value(), trade);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertThrows(CheckerException.class, () -> genericsProxyChecker.check(initiator));
        genericsProxyChecker.addNewChecker(userChecker);
        result = genericsProxyChecker.check(initiator);

        Assertions.assertEquals(result.value(), initiator);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertFalse(genericsProxyChecker.check(recipients).isSuccess());

        Assertions.assertThrows(CheckerException.class, () -> genericsProxyChecker.check(money));


    }


}