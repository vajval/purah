package org.purah.core.checker.base;


import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

import java.util.function.Predicate;

public class Checkers {
    public static Class<?> resultDataClass(Checker<?, ?> checker) {
        Class<?> result = generics(checker)[1].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    public static Class<?> supportInputCheckInstanceClass(Checker<?, ?> checker) {
        Class<?> result = generics(checker)[0].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    public static ResolvableType[] generics(Checker<?, ?> checker) {
        return ResolvableType
                .forClass(checker.getClass())
                .as(Checker.class)
                .getGenerics();
    }


    public static <T> BaseChecker<T, String> autoStringChecker(String name, Predicate<T> predicate, Class<T> clazz) {
        return new BaseChecker() {


            @Override
            public Class<?> resultClass() {
                return String.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public CheckResult<String> doCheck(CheckInstance checkInstance) {
                boolean test;
                try {
                    test = predicate.test((T) checkInstance.instance());
                } catch (Exception e) {
                    return error(checkInstance, e);
                }
                if (test) {
                    return success(checkInstance, "success");
                }
                return failed(checkInstance, "failed");
            }
        };
    }
}

