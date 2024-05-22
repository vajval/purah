package org.purah.core.checker.factory;


import org.purah.core.checker.Checker;

public interface CheckerFactory<T> {

    boolean match(String needMatchCheckerName);


    Checker createChecker(String needMatchCheckerName);



}
