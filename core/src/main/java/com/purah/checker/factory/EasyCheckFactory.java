package com.purah.checker.factory;

import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;

import java.util.function.Predicate;

public interface EasyCheckFactory<T> extends CheckerFactory {

    Predicate<T> predicate(String needMatchCheckerName);

    @Override
    default Checker createChecker(String needMatchCheckerName) {
        Predicate<T> predicate = predicate(needMatchCheckerName);
        return new BaseChecker<T, Object>() {
            @Override
            public CheckerResult<Object> doCheck(CheckInstance<T> checkInstance) {
                boolean test = predicate.test(checkInstance.instance());
                if (test) {
                    return success("success");
                } else {
                    return failed("failed");
                }
            }

            @Override
            public String name() {
                return needMatchCheckerName;
            }
        };
    }
}
