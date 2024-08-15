package io.github.vajval.purah.core.checker;

import io.github.vajval.purah.core.checker.converter.checker.AutoNull;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;

import java.lang.annotation.Annotation;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;


/*
 *  public static Checker<Long, Object> longChecker = LambdaChecker.of(Long.class).build("id1", i -> i == 1L);
 */
public class LambdaChecker<INPUT_ARG> implements Checker<INPUT_ARG, Object> {
    final String name;
    Function<InputToCheckerArg<INPUT_ARG>, CheckResult<?>> function;

    final Class<INPUT_ARG> clazz;
    AutoNull autoNull;

    private LambdaChecker(String name, Function<InputToCheckerArg<INPUT_ARG>, CheckResult<?>> function, Class<INPUT_ARG> clazz, AutoNull autoNull) {
        this.name = name;
        this.function = function;
        this.clazz = clazz;
        this.autoNull = autoNull;
    }

    public static <T> Builder<T> of(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public static class Builder<T> {
        final Class<T> clazz;
        AutoNull autoNull = AutoNull.notEnable;

        private Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        private Builder(Class<T> clazz, AutoNull autoNull) {
            this.clazz = clazz;
            this.autoNull = autoNull;
        }

        public LambdaChecker<T> build(Predicate<T> predicate) {
            return build(UUID.randomUUID().toString(), predicate);
        }

        public LambdaChecker<T> build(String name, Predicate<T> predicate) {
            Predicate<InputToCheckerArg<T>> predicate1 = tInputToCheckerArg -> predicate.test(tInputToCheckerArg.argValue());
            return buildWrap(name, predicate1);
        }

        public LambdaChecker<T> buildWrap(String name, Predicate<InputToCheckerArg<T>> predicate) {
            Function<InputToCheckerArg<T>, CheckResult<?>> function = inputToCheckerArg -> {
                if (predicate.test(inputToCheckerArg)) {
                    return LogicCheckResult.success();
                }
                return LogicCheckResult.failed();
            };
            return buildWrap(name, function);
        }

        public LambdaChecker<T> buildWrap(String name, Function<InputToCheckerArg<T>, CheckResult<?>> function) {
            return new LambdaChecker<>(name, function, clazz, autoNull);
        }


        public <A extends Annotation> LambdaChecker<T> annBuild(Class<A> annClazz, BiPredicate<A, T> biPredicate) {
            return annBuild(UUID.randomUUID().toString(), annClazz, biPredicate);
        }

        public <A extends Annotation> LambdaChecker<T> annBuild(String name, Class<A> annClazz, BiPredicate<A, T> biPredicate) {

            Function<InputToCheckerArg<T>, CheckResult<?>> function = inputToCheckerArg -> {
                T argValue = inputToCheckerArg.argValue();
                A a = inputToCheckerArg.annOnField(annClazz);
                if (biPredicate.test(a, argValue)) {
                    return LogicCheckResult.success();
                }
                return LogicCheckResult.failed();
            };
            return buildWrap(name, function);

        }


        public <A extends Annotation> LambdaChecker<T> annBuildWrap(String name, Class<A> annClazz,
                                                                    BiFunction<A, InputToCheckerArg<T>, CheckResult<?>> biFunction) {
            Function<InputToCheckerArg<T>, CheckResult<?>> function =
                    inputToCheckerArg -> biFunction.apply(inputToCheckerArg.annOnField(annClazz), inputToCheckerArg);
            return buildWrap(name, function);
        }

        public <A extends Annotation> LambdaChecker<T> annBuildWrap(String name, Class<A> annClazz, BiPredicate<A, InputToCheckerArg<T>> biPredicate) {
            Predicate<InputToCheckerArg<T>> predicate =
                    inputToCheckerArg -> biPredicate.test(inputToCheckerArg.annOnField(annClazz), inputToCheckerArg);
            return buildWrap(name, predicate);
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
        if (inputToCheckerArg.isNull() && autoNull != AutoNull.notEnable) {
            if (autoNull == AutoNull.failed) {
                return LogicCheckResult.failed();
            }
            if (autoNull == AutoNull.success) {
                return LogicCheckResult.success();
            }
            if (autoNull == AutoNull.ignore) {
                return LogicCheckResult.ignore();
            }
        }
        return (CheckResult) function.apply(inputToCheckerArg);
    }

    @Override
    public Class<?> resultDataClass() {
        return Object.class;
    }
}
