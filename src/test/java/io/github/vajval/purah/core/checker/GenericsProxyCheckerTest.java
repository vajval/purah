package io.github.vajval.purah.core.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.CheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.util.User;

public class GenericsProxyCheckerTest {
    public static final Checker<User, Object> userChecker = LambdaChecker.of(User.class).build("id1", i -> i.getId().equals(1L));

    public static final Checker<Long, Object> longChecker = LambdaChecker.of(Long.class).build("id1", i -> i == 1L);
    public static final Checker<Integer, Object> intChecker = LambdaChecker.of(int.class).build("id1", i -> i == 1);


    @Test
    void get3() {
        GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createByChecker(intChecker);
        Assertions.assertTrue(genericsProxyChecker.oCheck(1));
        Assertions.assertFalse(genericsProxyChecker.oCheck(2));
        Assertions.assertThrows(CheckException.class,()-> genericsProxyChecker.check(null));
    }


    @Test
    void get() {

        GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createByChecker(longChecker);

        CheckResult<Object> result = genericsProxyChecker.oCheck(1L);
        Assertions.assertTrue(result);


        Assertions.assertThrows(CheckException.class, () -> genericsProxyChecker.oCheck(User.GOOD_USER));

        genericsProxyChecker.addNewChecker(userChecker);
        result = genericsProxyChecker.oCheck(User.GOOD_USER);

        Assertions.assertTrue(result);

        Assertions.assertFalse(genericsProxyChecker.oCheck(User.BAD_USER));

        Assertions.assertThrows(CheckException.class, () -> genericsProxyChecker.oCheck("123"));


    }


}