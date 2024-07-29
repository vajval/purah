package io.github.vajval.purah.core.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.CheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.factory.LambdaCheckerFactory;

import java.util.Objects;

public class CheckerManagerTest {


    CheckerManager checkerManager;

    @BeforeEach
    void beforeEach() {
        checkerManager =new CheckerManager();
    }


    @Test
    void regAndFactoryTest() {

        checkerManager.reg(GenericsProxyCheckerTest.userChecker);
        GenericsProxyChecker genericsProxyChecker = checkerManager.of("id1");

        Assertions.assertThrows(CheckException.class, () -> checkerManager.of("id1").check(1L));//no long


        checkerManager.reg(GenericsProxyCheckerTest.longChecker);
        Assertions.assertTrue(genericsProxyChecker.check(1L));//have long


        Assertions.assertThrows(CheckException.class, () -> genericsProxyChecker.check(1));//no int
        LambdaCheckerFactory<Integer> intCheckerFactory = LambdaCheckerFactory.of(Integer.class).build("id*", (a, b) -> {
            Integer name = Integer.parseInt(a.replace("id", ""));
            return Objects.equals(name, b);
        });
        checkerManager.addCheckerFactory(intCheckerFactory);
        Assertions.assertTrue(genericsProxyChecker.check(1));                           //have int


        Assertions.assertThrows(CheckException.class, () -> genericsProxyChecker.check("2"));//no String
        LambdaCheckerFactory<String> stringCheckerFactory = LambdaCheckerFactory.of(String.class).build("id*",
                (a, b) -> Objects.equals(a.replace("id", ""), b));
        checkerManager.addCheckerFactory(stringCheckerFactory);
        Assertions.assertTrue(checkerManager.of("id1").check("1"));   //have string

        Assertions.assertThrows(CheckException.class, () -> checkerManager.of("id2").check(2L));//no long


        CheckResult<Object> result = checkerManager.of("id5").check(5);
        Assertions.assertTrue(result);
        result = checkerManager.of("id100").check("100");
        Assertions.assertTrue(result);
        result = checkerManager.of("id4396").check(2200);
        Assertions.assertFalse(result);
    }

    public static boolean id(String name, long id) {
        long parseId = Long.parseLong(name.replace("id", ""));

        return parseId == id;
    }

    public static boolean id(String name, Integer id) {
        long parseId = Long.parseLong(name.replace("id", ""));
        return parseId == id.longValue();
    }


}