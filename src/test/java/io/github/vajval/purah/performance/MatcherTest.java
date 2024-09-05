package io.github.vajval.purah.performance;

import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.util.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.Map;

import static io.github.vajval.purah.util.User.GOOD_USER_BAD_CHILD;

public class MatcherTest {

    public String run(String str, boolean unsafeCache) {
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher(str);
        InputToCheckerArg<User> inputToCheckerArg = InputToCheckerArg.of(GOOD_USER_BAD_CHILD);
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        reflectArgResolver.enableExtendUnsafeCache(unsafeCache);
        int num = 3_0_000;
//           int num = 1_000;
        for (int i = 0; i < num; i++) {
            reflectArgResolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
        }
        StopWatch stopWatch = new StopWatch("123");
        stopWatch.start(String.valueOf(unsafeCache));
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = null;
        for (int i = 0; i < num * 1000; i++) {
            matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
        }
        System.out.println(matchFieldObjectMap.keySet());
        stopWatch.stop();
        return stopWatch.prettyPrint();
    }

    @Test
    public void generalWithCache2() {

        System.out.println(run("*", false));
    }

    //三千万八秒
    @Test
    public void generalWithCache() {
        System.out.println(run("*|childUser.id|childUser.name|childUser.phone|childUser.age", true));
    }

//    @Test
//    public void generalWithOutCache() {
//        System.out.println(run("*|childUser.id|childUser.name|childUser.phone|childUser.age", false));
//    }
    @Test
    public void treeTest(){

    }
}
