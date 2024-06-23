package org.purah.core.checker.factory;

import org.purah.core.checker.Checker;
import org.purah.core.checker.LambdaChecker;
import org.purah.core.matcher.WildCardMatcher;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LambdaCheckerFactory<INPUT_ARG> implements CheckerFactory {


    BiPredicate<String, INPUT_ARG> checkLogic;

    Predicate<String> matchLogic;
    Class<INPUT_ARG> clazz;


    private LambdaCheckerFactory(Predicate<String> matchLogic,
                                 BiPredicate<String, INPUT_ARG> checkLogic, Class<INPUT_ARG> clazz) {
        this.checkLogic = checkLogic;
        this.clazz = clazz;
        this.matchLogic = matchLogic;
    }


    @Override
    public boolean match(String needMatchCheckerName) {
        return matchLogic.test(needMatchCheckerName);
    }

    @Override
    public Checker<INPUT_ARG, Object> createChecker(String needMatchCheckerName) {
        return LambdaChecker.of(clazz).build(needMatchCheckerName,
                inputArg -> checkLogic.test(needMatchCheckerName, inputArg));
    }

    public static <T> Builder<T> of(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public static class Builder<T> {
        Class<T> clazz;

        private Builder(Class<T> clazz) {
            this.clazz = clazz;
        }


        public LambdaCheckerFactory<T> build(Predicate<String> matchLogic, BiPredicate<String, T> predicate) {
            return new LambdaCheckerFactory<>(matchLogic, predicate, clazz);
        }

        public LambdaCheckerFactory<T> build(String matchStr, BiPredicate<String, T> predicate) {
            Predicate<String> matchLogic = i -> new WildCardMatcher(matchStr).match(i);
            return build(matchLogic, predicate);

        }
    }

}
