package org.purah.core.checker.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.Checker;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.LambdaChecker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckerException;
import org.purah.util.User;

public class GenericsProxyCheckerTest {
    public static Checker<User, Object> userChecker =
            LambdaChecker.of(User.class).build("id1", i -> i.getId().equals(1L));


    public static Checker<Long, Object> LongChecker =
            LambdaChecker.of(Long.class).build("id1", i -> i == 1L);


    @Test
    void get() {

        GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createByChecker(LongChecker);

        CheckResult<Object> result = genericsProxyChecker.check( 1L);
        Assertions.assertTrue(result);


        Assertions.assertThrows(CheckerException.class, () -> genericsProxyChecker.check(User.GOOD_USER));

        genericsProxyChecker.addNewChecker(userChecker);
        result = genericsProxyChecker.check(User.GOOD_USER);

        Assertions.assertTrue(result);

        Assertions.assertFalse(genericsProxyChecker.check(User.BAD_USER));

        Assertions.assertThrows(CheckerException.class, () -> genericsProxyChecker.check("123"));


    }


}