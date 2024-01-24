package com.purah.checker;

import com.purah.Util;
import com.purah.base.Name;
import com.purah.checker.context.SingleCheckerResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class MethodCheckerTest {


    static class TestCheckers {
        @Name("id为1")
        public boolean checkById(Long id) {
            return id.equals(1L);
        }

        @Name("id为1")
        public SingleCheckerResult<String> checkById(CheckInstance<Long> id) {
            boolean test = checkById(id.instance());
            if (test) {
                return SingleCheckerResult.success("success", "success");
            } else {
                return SingleCheckerResult.failed("failed", "failed");
            }
        }

        @Name("id为1")
        public SingleCheckerResult<String> checkByUser(Util.User user) {
            return checkById(CheckInstance.create(user.getId()));

        }

    }

    @Test
    void name() throws NoSuchMethodException {

        TestCheckers testCheckers = new TestCheckers();
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
        MethodChecker methodChecker = new MethodChecker(testCheckers, testMethod);
        Assertions.assertTrue(methodChecker.check(CheckInstance.create(1L)).isSuccess());

        testMethod = TestCheckers.class.getMethod("checkById", CheckInstance.class);
        methodChecker = new MethodChecker(testCheckers, testMethod);
        Assertions.assertTrue(methodChecker.check(CheckInstance.create(1L)).isSuccess());

    }
    @Test
    void e() throws NoSuchMethodException {
        Method testMethod = TestCheckers.class.getMethod("checkById", Long.class);
        System.out.println(testMethod.getDeclaredAnnotation(com.purah.base.Name.class).value());
    }
    @Test
    void inputCheckInstanceClass() throws NoSuchMethodException {
        CheckerManager checkerManager = new CheckerManager();
        TestCheckers testCheckers = new TestCheckers();

        MethodChecker methodChecker = new MethodChecker(testCheckers, TestCheckers.class.getMethod("checkById", Long.class));
        checkerManager.reg(methodChecker);

        methodChecker = new MethodChecker(testCheckers, TestCheckers.class.getMethod("checkById", CheckInstance.class));
        checkerManager.reg(methodChecker);
        methodChecker = new MethodChecker(testCheckers, TestCheckers.class.getMethod("checkByUser", Util.User.class));
        checkerManager.reg(methodChecker);
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