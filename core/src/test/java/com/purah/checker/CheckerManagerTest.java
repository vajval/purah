package com.purah.checker;

import com.purah.checker.context.SingleCheckerResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.purah.Util.*;

class CheckerManagerTest {


    static Checker<Trade, String> tradeChecker =
            Checkers.autoStringChecker("发起者张三检测", i -> i.getInitiator().getName().equals("张三"), Trade.class);

    static Checker<User, String> userChecker =
            Checkers.autoStringChecker("发起者张三检测", i -> i.getName().equals("张三"), User.class);


    @Test
    void get() {
        CheckerManager checkerManager = new CheckerManager();

        checkerManager.reg(tradeChecker);
        checkerManager.reg(userChecker);

        Checker checker = checkerManager.get("发起者张三检测");
        Assertions.assertTrue(checker.check(CheckInstance.create(trade)).success());
        Assertions.assertTrue(checker.check(CheckInstance.create(initiator)).success());

        Assertions.assertFalse(checker.check(CheckInstance.create(recipients)).success());
        Assertions.assertTrue(checker.check(CheckInstance.create(recipients)).failed());

        Assertions.assertThrows(RuntimeException.class, () -> checker.check(CheckInstance.create(money)).success());


    }


    @Test
    void reg() {


    }


}