package org.purah.core.checker.base;


import org.purah.core.checker.result.CheckResult;

import java.util.function.Predicate;

public class Checkers {


    public static <T> BaseChecker<T, String> autoStringChecker(String name, Predicate<T> predicate, Class<T> clazz) {
        return new BaseChecker() {


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

