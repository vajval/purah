package com.purah.checker.factory;

import com.purah.checker.Checker;

import java.util.function.Predicate;

public interface CheckerFactory<T> {

    boolean match(String needMatchCheckerName);


    Checker createChecker(String needMatchCheckerName);



}
