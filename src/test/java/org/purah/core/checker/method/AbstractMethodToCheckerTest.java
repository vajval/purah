package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.name.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.converter.checker.AbstractWrapMethodToChecker;
import org.purah.core.checker.converter.checker.ByLogicMethodChecker;
import org.purah.core.checker.result.BaseLogicCheckResult;

import java.lang.reflect.Method;

class AbstractMethodToCheckerTest {


    public static class TestCheckers {
        @Name("id为1")
        public boolean checkById(Long id) {
            return id.equals(1L);
        }

        @Name("id为1")
        public BaseLogicCheckResult<String> checkById(InputToCheckerArg<Long> id) {
            boolean test = checkById(id.argValue());
            if (test) {
                return BaseLogicCheckResult.success();
            } else {
                return BaseLogicCheckResult.failed("failed", "failed");
            }
        }

        @Name("id为1")
        public BaseLogicCheckResult checkByUser(Util.User user) {
            return checkById(InputToCheckerArg.of(user.getId()));

        }

    }



    @Test
    void name() throws NoSuchMethodException {

        TestCheckers testCheckers = new TestCheckers();
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
        AbstractWrapMethodToChecker abstractMethodToChecker = new ByLogicMethodChecker(testCheckers, testMethod);
        Assertions.assertTrue(abstractMethodToChecker.check(1L).isSuccess());

        testMethod = TestCheckers.class.getMethod("checkById", InputToCheckerArg.class);
        abstractMethodToChecker = new ByLogicMethodChecker(testCheckers, testMethod);
        Assertions.assertTrue(abstractMethodToChecker.check(1L).isSuccess());

    }

    @Test
    void e() throws NoSuchMethodException {
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
    }

    @Test
    void inputCheckInstanceClass() throws NoSuchMethodException {
        CheckerManager checkerManager = new CheckerManager();
        TestCheckers testCheckers = new TestCheckers();

        AbstractWrapMethodToChecker abstractMethodToChecker = new ByLogicMethodChecker(testCheckers, TestCheckers.class.getMethod("checkById", Long.class));
        checkerManager.reg(abstractMethodToChecker);

        abstractMethodToChecker = new ByLogicMethodChecker(testCheckers, TestCheckers.class.getMethod("checkById", InputToCheckerArg.class));
        checkerManager.reg(abstractMethodToChecker);
        abstractMethodToChecker = new ByLogicMethodChecker(testCheckers, TestCheckers.class.getMethod("checkByUser", Util.User.class));
        checkerManager.reg(abstractMethodToChecker);
        GenericsProxyChecker genericsProxyChecker = checkerManager.of("id为1");
        Assertions.assertTrue(genericsProxyChecker.check(Util.initiator).isSuccess());
        Assertions.assertFalse(genericsProxyChecker.check(Util.recipients).isSuccess());


        Assertions.assertTrue(genericsProxyChecker.check(1L).isSuccess());
        Assertions.assertFalse(genericsProxyChecker.check(2L).isSuccess());
    }

    @Test
    void resultClass() {
    }

    @Test
    void doCheck() {
    }
}