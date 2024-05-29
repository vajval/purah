package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.base.Checkers;

import static org.purah.core.Util.*;

class CheckerManagerTest {


    static Checker<Util.Trade, String> tradeChecker =
            Checkers.autoStringChecker("发起者张三检测", i -> i.getInitiator().getName().equals("张三"), Trade.class);

    static Checker<User, String> userChecker =
            Checkers.autoStringChecker("发起者张三检测", i -> i.getName().equals("张三"), User.class);


    @Test
    void get() {
        CheckerManager checkerManager = new CheckerManager();

        checkerManager.reg(tradeChecker);
        checkerManager.reg(userChecker);

        Checker checker = checkerManager.get("发起者张三检测");
        Assertions.assertTrue(checker.check(CheckInstance.createObjectInstance(trade)).isSuccess());
        Assertions.assertTrue(checker.check(CheckInstance.createObjectInstance(initiator)).isSuccess());

        Assertions.assertFalse(checker.check(CheckInstance.createObjectInstance(recipients)).isSuccess());
        Assertions.assertTrue(checker.check(CheckInstance.createObjectInstance(recipients)).isFailed());

        Assertions.assertThrows(RuntimeException.class, () -> checker.check(CheckInstance.createObjectInstance(money)).isSuccess());


    }


    @Test
    void reg() {


    }


}