package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.base.Name;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.base.ExecChecker;
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
        public BaseLogicCheckResult<String> checkById(CheckInstance<Long> id) {
            boolean test = checkById(id.instance());
            if (test) {
                return BaseLogicCheckResult.success("success", "success");
            } else {
                return BaseLogicCheckResult.failed("failed", "failed");
            }
        }

        @Name("id为1")
        public BaseLogicCheckResult checkByUser(Util.User user) {
            return checkById(CheckInstance.createObjectInstance(user.getId()));

        }

    }



    @Test
    void name() throws NoSuchMethodException {

        TestCheckers testCheckers = new TestCheckers();
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
        AbstractMethodToChecker abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, testMethod);
        System.out.println(abstractMethodToChecker.check(CheckInstance.createObjectInstance(1L)));
        Assertions.assertTrue(abstractMethodToChecker.check(CheckInstance.createObjectInstance(1L)).isSuccess());

        testMethod = TestCheckers.class.getMethod("checkById", CheckInstance.class);
        abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, testMethod);
        Assertions.assertTrue(abstractMethodToChecker.check(CheckInstance.createObjectInstance(1L)).isSuccess());

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

        abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, TestCheckers.class.getMethod("checkById", CheckInstance.class));
        checkerManager.reg(abstractMethodToChecker);
        abstractMethodToChecker = new CheckerByLogicMethod(testCheckers, TestCheckers.class.getMethod("checkByUser", Util.User.class));
        checkerManager.reg(abstractMethodToChecker);
        ExecChecker execChecker = checkerManager.get("id为1");
        Assertions.assertTrue(execChecker.check(CheckInstance.createObjectInstance(Util.initiator)).isSuccess());
        Assertions.assertFalse(execChecker.check(CheckInstance.createObjectInstance(Util.recipients)).isSuccess());


        Assertions.assertTrue(execChecker.check(CheckInstance.createObjectInstance(1L)).isSuccess());
        Assertions.assertFalse(execChecker.check(CheckInstance.createObjectInstance(2L)).isSuccess());
    }

    @Test
    void resultClass() {
    }

    @Test
    void doCheck() {
    }
}