package com.purah.checker.factory;

import com.purah.checker.Checker;

public interface CheckerFactory {

    boolean match(String needMatchCheckerName);


    Checker createChecker(String needMatchCheckerName);


}
