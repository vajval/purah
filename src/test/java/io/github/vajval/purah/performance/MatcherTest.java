package io.github.vajval.purah.performance;

import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.util.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import static io.github.vajval.purah.util.User.GOOD_USER_BAD_CHILD;

public class MatcherTest {

    public String run(String str,boolean unsafeCache) {
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher(str);
        InputToCheckerArg<User> inputToCheckerArg = InputToCheckerArg.of(GOOD_USER_BAD_CHILD);
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        reflectArgResolver.enableExtendUnsafeCache(unsafeCache);
//        int num = 1_0_000;
           int num = 1_000;
        for (int i = 0; i < num; i++) {
            reflectArgResolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
        }
        StopWatch stopWatch = new StopWatch("123");
        stopWatch.start(String.valueOf(unsafeCache));
        for (int i = 0; i < num * 1000; i++) {
            reflectArgResolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
        }
        stopWatch.stop();
        return stopWatch.prettyPrint();
    }
    //一千万条7秒
    @Test
    public void generalWithCache2() {

        System.out.println(run("*",false));
    }

    //一千万条10秒
    @Test
    public void generalWithCache() {
        System.out.println(run("*|childUser.id|childUser.name|childUser.phone|childUser.age",true));
    }

    //一千万条25秒
    @Test
    public void generalWithOutCache() {
        System.out.println(run("*|childUser.id|childUser.name|childUser.phone|childUser.age",false));
    }
}
