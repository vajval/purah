package org.purah.core.checker.factory;



import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.result.CheckResult;

import java.util.function.Predicate;

public interface EasyCheckFactory<T> extends CheckerFactory {

    Predicate<T> predicate(String needMatchCheckerName);

    @Override
    default Checker createChecker(String needMatchCheckerName) {
        Predicate<T> predicate = predicate(needMatchCheckerName);
        return new BaseSupportCacheChecker<T, Object>() {
            @Override
            public CheckResult<Object> doCheck(CheckInstance<T> checkInstance) {
                boolean test = predicate.test(checkInstance.instance());
                if (test) {
                    return success(checkInstance,"success");
                } else {
                    return failed(checkInstance,"failed");
                }
            }

            @Override
            public String name() {
                return needMatchCheckerName;
            }
        };
    }
}
