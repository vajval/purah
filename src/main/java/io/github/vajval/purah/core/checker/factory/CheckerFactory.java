package io.github.vajval.purah.core.checker.factory;


import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.name.IName;

public interface CheckerFactory extends IName {

    boolean match(String needMatchCheckerName);

    Checker<?,?> createChecker(String needMatchCheckerName);

    default boolean cacheBeCreatedChecker() {
        return true;
    }


}
