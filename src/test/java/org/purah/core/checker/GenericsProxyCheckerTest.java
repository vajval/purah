package org.purah.core.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckException;
import org.purah.util.User;

public class GenericsProxyCheckerTest {
    public static Checker<User, Object> userChecker = LambdaChecker.of(User.class).build("id1", i -> i.getId().equals(1L));


    public static Checker<Long, Object> longChecker = LambdaChecker.of(Long.class).build("id1", i -> i == 1L);
    public static Checker<Integer, Object> intChecker = LambdaChecker.of(Integer.class).build("id1", i -> i == 1);


    @Test
    void get() {

        GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createByChecker(longChecker);

        CheckResult<Object> result = genericsProxyChecker.check(1L);
        Assertions.assertTrue(result);


        Assertions.assertThrows(CheckException.class, () -> genericsProxyChecker.check(User.GOOD_USER));

        genericsProxyChecker.addNewChecker(userChecker);
        result = genericsProxyChecker.check(User.GOOD_USER);

        Assertions.assertTrue(result);

        Assertions.assertFalse(genericsProxyChecker.check(User.BAD_USER));

        Assertions.assertThrows(CheckException.class, () -> genericsProxyChecker.check("123"));


    }


}