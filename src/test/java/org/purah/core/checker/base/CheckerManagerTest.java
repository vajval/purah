package org.purah.core.checker.base;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.Util;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.base.Checkers;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.factory.DefaultMethodToCheckerFactory;
import org.purah.core.checker.factory.MethodToCheckerFactory;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckerException;
import org.purah.core.exception.CheckerRegException;

import java.nio.file.LinkOption;
import java.util.Objects;

import static org.purah.core.Util.*;

public class CheckerManagerTest {
    CheckerManager checkerManager;
    MethodToCheckerFactory methodToCheckerFactory = new DefaultMethodToCheckerFactory();

    public static boolean id(String name, long id) {
        long parseId = Long.parseLong(name.replace("id", ""));

        return parseId == id;
    }

    public static boolean id(String name, Integer id) {
        long parseId = Long.parseLong(name.replace("id", ""));

        return parseId == id.longValue();
    }

    @BeforeEach
    void beforeEach() throws NoSuchMethodException {
        checkerManager = new CheckerManager();
    }


    @Test
    void reg() throws NoSuchMethodException {
        checkerManager.reg(GenericsProxyCheckerTest.userChecker);
        checkerManager.reg(GenericsProxyCheckerTest.tradeChecker);

        Assertions.assertThrows(CheckerRegException.class, () -> checkerManager.get("id2"));
        checkerManager.addCheckerFactory(methodToCheckerFactory.toCheckerFactory(null, CheckerManagerTest.class.getMethod("id", String.class, Integer.class), "id*", true));

        Assertions.assertThrows(CheckerException.class, () -> checkerManager.get("id2").check(2L));
        Assertions.assertDoesNotThrow(() -> checkerManager.get("id2").check(2));

        checkerManager.addCheckerFactory(methodToCheckerFactory.toCheckerFactory(null, CheckerManagerTest.class.getMethod("id", String.class, long.class), "id*", true));

        CheckResult<Object> result = checkerManager.get("id2").check(2L);
        Assertions.assertTrue(result.isSuccess());
        result = checkerManager.get("id3").check(3L);
        Assertions.assertTrue(result.isSuccess());
        result = checkerManager.get("id5").check(5);
        Assertions.assertTrue(result.isSuccess());
        result = checkerManager.get("id100").check(100L);
        Assertions.assertTrue(result.isSuccess());
        result = checkerManager.get("id4396").check(7891);
        Assertions.assertTrue(result.isFailed());
    }


}