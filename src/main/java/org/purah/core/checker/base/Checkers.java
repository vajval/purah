package org.purah.core.checker.base;


import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.Checker;
import org.purah.core.checker.method.converter.DefaultMethodToCheckerConverter;
import org.purah.core.checker.method.converter.MethodToCheckerConverter;
import org.purah.core.checker.factory.method.converter.DefaultMethodToCheckerFactoryConverter;
import org.purah.core.checker.factory.method.converter.MethodToCheckerFactoryConverter;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

import java.util.function.Function;
import java.util.function.Predicate;

public class Checkers {


    public static <T> AbstractBaseSupportCacheChecker<T, Object> autoStringChecker(String name, Predicate<T> predicate, Class<T> clazz) {
        return new AbstractBaseSupportCacheChecker() {


            @Override
            public Class<?> inputArgClass() {
                return clazz;
            }

            @Override
            public Class<?> resultDataClass() {
                return String.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public CheckResult<String> doCheck(InputToCheckerArg inputToCheckerArg) {
                boolean test;
                try {
                    test = predicate.test((T) inputToCheckerArg.argValue());
                } catch (Exception e) {
                    return error(inputToCheckerArg, e);
                }
                if (test) {
                    return success(inputToCheckerArg, inputToCheckerArg.argValue());
                }
                return failed(inputToCheckerArg, inputToCheckerArg.argValue());
            }
        };
    }
}

