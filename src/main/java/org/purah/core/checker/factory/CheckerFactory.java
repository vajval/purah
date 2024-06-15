package org.purah.core.checker.factory;


import org.purah.core.base.IName;
import org.purah.core.checker.base.Checker;

public interface CheckerFactory extends IName {

    boolean match(String needMatchCheckerName);

    Checker createChecker(String needMatchCheckerName);

    default boolean cacheBeCreatedChecker() {
        return true;
    }


}
