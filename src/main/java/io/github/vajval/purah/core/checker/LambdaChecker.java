package io.github.vajval.purah.core.checker;

import com.google.common.collect.Maps;
import io.github.vajval.purah.core.checker.converter.checker.AutoNull;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
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

    String failedInfo;
    String logicFrom;

    boolean failedInfoReplace;

    private LambdaChecker(String name, Function<InputToCheckerArg<INPUT_ARG>, CheckResult<?>> function,
                          Class<INPUT_ARG> clazz, AutoNull autoNull, String failedInfo, String logicFrom) {
        this.name = name;
        this.function = function;
        this.clazz = clazz;
        this.autoNull = autoNull;
        this.failedInfo = failedInfo;
        this.logicFrom = logicFrom;
        this.failedInfoReplace = this.failedInfo.contains("$");
    }


    public static <T> Builder<T> of(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public static <T> Builder<T> of(Class<T> clazz, AutoNull autoNull) {
        return new Builder<>(clazz, autoNull);
    }

    public static class Builder<T> {
        final Class<T> clazz;
        AutoNull autoNull = AutoNull.notEnable;

        String failedInfo = "failed";
        String logicFrom = "create by lambdaChecker";

        private Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        private Builder(Class<T> clazz, AutoNull autoNull) {
            this.clazz = clazz;
            this.autoNull = autoNull;
        }

        public Builder<T> failedInfo(String failedInfo) {
            this.failedInfo = failedInfo;
            return this;
        }

        public Builder<T> logicFrom(String logicFrom) {
            this.logicFrom = logicFrom;
            return this;
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
            return new LambdaChecker<>(name, function, clazz, autoNull, failedInfo, logicFrom);
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
                String resultFailedInfo = failedInfo;
                if (failedInfoReplace) {
                    resultFailedInfo = resultFailedInfo(failedInfo, inputToCheckerArg);
                }
                return LogicCheckResult.failed().setInfo(resultFailedInfo);
            }
            if (autoNull == AutoNull.success) {
                return LogicCheckResult.success();
            }
            if (autoNull == AutoNull.ignore) {
                return LogicCheckResult.ignore();
            }
        }
        CheckResult<?> result = function.apply(inputToCheckerArg);
        String resultFailedInfo = failedInfo;
        if (failedInfoReplace) {
            resultFailedInfo = resultFailedInfo(failedInfo, inputToCheckerArg);
        }
        result.setCheckerLogicFrom(logicFrom);
        result.setInfo(resultFailedInfo);
        return (CheckResult) result;
    }

    private static <T> String resultFailedInfo(String failedInfo, InputToCheckerArg<T> inputToCheckerArg) {
        Map<String, String> params = Maps.newHashMapWithExpectedSize(2);
        if (StringUtils.hasText(inputToCheckerArg.fieldPath)) {
            params.put("path", inputToCheckerArg.fieldPath);
        }
        params.put("arg", String.valueOf(inputToCheckerArg.argValue()));
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(failedInfo);

    }

    @Override
    public Class<?> resultDataClass() {
        return Object.class;
    }
}
