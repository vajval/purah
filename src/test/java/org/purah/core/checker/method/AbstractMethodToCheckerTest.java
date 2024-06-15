package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.base.Name;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.base.GenericsProxyChecker;
import org.purah.core.checker.method.toChecker.CheckerByLogicMethod;
import org.purah.core.checker.method.toChecker.AbstractMethodToChecker;
import org.purah.core.checker.result.BaseLogicCheckResult;

import java.lang.reflect.Method;

class AbstractMethodToCheckerTest {


    public static class TestCheckers {
        @Name("id为1")
        public boolean checkById(Long id) {
            return id.equals(1L);
        }

        @Name("id为1")
        public BaseLogicCheckResult<String> checkById(InputCheckArg<Long> id) {
            boolean test = checkById(id.inputArg());
            if (test) {
                return BaseLogicCheckResult.success("success", "success");
            } else {
                return BaseLogicCheckResult.failed("failed", "failed");
            }
        }

        @Name("id为1")
        public BaseLogicCheckResult checkByUser(Util.User user) {
            return checkById(InputCheckArg.create(user.getId()));

        }

    }



    @Test
    void name() throws NoSuchMethodException {

        TestCheckers testCheckers = new TestCheckers();
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
        AbstractMethodToChecker abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, testMethod);
        System.out.println(abstractMethodToChecker.check(InputCheckArg.create(1L)));
        Assertions.assertTrue(abstractMethodToChecker.check(InputCheckArg.create(1L)).isSuccess());

        testMethod = TestCheckers.class.getMethod("checkById", InputCheckArg.class);
        abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, testMethod);
        Assertions.assertTrue(abstractMethodToChecker.check(InputCheckArg.create(1L)).isSuccess());

    }

    @Test
    void e() throws NoSuchMethodException {
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
    }

    @Test
    void inputCheckInstanceClass() throws NoSuchMethodException {
        CheckerManager checkerManager = new CheckerManager();
        TestCheckers testCheckers = new TestCheckers();

        AbstractMethodToChecker abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, TestCheckers.class.getMethod("checkById", Long.class));
        checkerManager.reg(abstractMethodToChecker);

        abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, TestCheckers.class.getMethod("checkById", InputCheckArg.class));
        checkerManager.reg(abstractMethodToChecker);
        abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, TestCheckers.class.getMethod("checkByUser", Util.User.class));
        checkerManager.reg(abstractMethodToChecker);
        GenericsProxyChecker genericsProxyChecker = checkerManager.get("id为1");
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