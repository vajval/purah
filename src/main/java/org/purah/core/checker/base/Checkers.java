package org.purah.core.checker.base;


import org.purah.core.base.NameUtil;
import org.purah.core.checker.method.DefaultMethodToChecker;
import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.checker.factory.DefaultMethodToCheckerFactory;
import org.purah.core.checker.factory.MethodToCheckerFactory;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class Checkers {
    public static final MethodToChecker defaultMethodToChecker = new DefaultMethodToChecker();
    public static final MethodToCheckerFactory defaultMethodToCheckerFactory = new DefaultMethodToCheckerFactory();


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


    public static <T> BaseSupportCacheChecker<T, Object> autoStringChecker(String name, Predicate<T> predicate, Class<T> clazz) {
        return new BaseSupportCacheChecker() {


            @Override
            public Class<?> inputCheckInstanceClass() {
                return clazz;
            }

            @Override
            public Class<?> resultClass() {
                return String.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public CheckResult<String> doCheck(InputCheckArg inputCheckArg) {
                boolean test;
                try {
                    test = predicate.test((T) inputCheckArg.inputArg());
                } catch (Exception e) {
                    return error(inputCheckArg, e);
                }
                if (test) {
                    return success(inputCheckArg, inputCheckArg.inputArg());
                }
                return failed(inputCheckArg, inputCheckArg.inputArg());
            }
        };
    }
}

