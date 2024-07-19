package org.purah.core.checker;

import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.LogicCheckResult;

import java.util.function.Predicate;

public class LambdaChecker<INPUT_ARG> implements Checker<INPUT_ARG, Object> {

    final String name;
    final Predicate<INPUT_ARG> predicate;
    final Class<INPUT_ARG> clazz;

    private LambdaChecker(String name, Predicate<INPUT_ARG> predicate, Class<INPUT_ARG> clazz) {
        this.name = name;
        this.predicate = predicate;
        this.clazz = clazz;

    }


    public static <T> Builder<T> of(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public static class Builder<T> {
        final Class<T> clazz;

        private Builder(Class<T> clazz) {
            this.clazz = clazz;
        }


        public LambdaChecker<T> build(String name, Predicate<T> predicate) {
            return new LambdaChecker<>(name, predicate, clazz);
        }

        public LambdaChecker<T> build(Predicate<T> predicate) {
            return build(LambdaChecker.class.getName(), predicate);

        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> inputArgClass() {
        return clazz;
    }

    @Override
    public CheckResult<Object> check(InputToCheckerArg<INPUT_ARG> inputToCheckerArg) {
        INPUT_ARG inputArg = inputToCheckerArg.argValue();
        if (predicate.test(inputArg)) {
            return LogicCheckResult.successBuildLog(inputToCheckerArg, inputArg);
        }
        return LogicCheckResult.failedBuildLog(inputToCheckerArg, inputArg);
    }
}
