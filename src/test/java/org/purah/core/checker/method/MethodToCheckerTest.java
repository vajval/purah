package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.base.Name;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.ExecChecker;
import org.purah.core.checker.result.BaseLogicCheckResult;

import java.lang.reflect.Method;

class MethodToCheckerTest {


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
            return checkById(CheckInstance.create(user.getId()));

        }

    }



    @Test
    void name() throws NoSuchMethodException {

        TestCheckers testCheckers = new TestCheckers();
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
        MethodToChecker methodToChecker = new BaseLogicMethodToChecker(testCheckers, testMethod);
        System.out.println(methodToChecker.check(CheckInstance.create(1L)));
        Assertions.assertTrue(methodToChecker.check(CheckInstance.create(1L)).isSuccess());

        testMethod = TestCheckers.class.getMethod("checkById", CheckInstance.class);
        methodToChecker = new BaseLogicMethodToChecker(testCheckers, testMethod);
        Assertions.assertTrue(methodToChecker.check(CheckInstance.create(1L)).isSuccess());

    }

    @Test
    void e() throws NoSuchMethodException {
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
    }

    @Test
    void inputCheckInstanceClass() throws NoSuchMethodException {
        CheckerManager checkerManager = new CheckerManager();
        TestCheckers testCheckers = new TestCheckers();

        MethodToChecker methodToChecker = new BaseLogicMethodToChecker(testCheckers, TestCheckers.class.getMethod("checkById", Long.class));
        checkerManager.reg(methodToChecker);

        methodToChecker = new BaseLogicMethodToChecker(testCheckers, TestCheckers.class.getMethod("checkById", CheckInstance.class));
        checkerManager.reg(methodToChecker);
        methodToChecker = new BaseLogicMethodToChecker(testCheckers, TestCheckers.class.getMethod("checkByUser", Util.User.class));
        checkerManager.reg(methodToChecker);
        ExecChecker execChecker = checkerManager.get("id为1");
        Assertions.assertTrue(execChecker.check(CheckInstance.create(Util.initiator)).isSuccess());
        Assertions.assertFalse(execChecker.check(CheckInstance.create(Util.recipients)).isSuccess());


        Assertions.assertTrue(execChecker.check(CheckInstance.create(1L)).isSuccess());
        Assertions.assertFalse(execChecker.check(CheckInstance.create(2L)).isSuccess());
    }

    @Test
    void resultClass() {
    }

    @Test
    void doCheck() {
    }
}