package com.purah.checker.factory;

import com.purah.checker.Checker;

import java.lang.reflect.Method;

public class MethodToCheckerFactory implements CheckerFactory {
    Method method;
    Object bean;

    public MethodToCheckerFactory() {

    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return false;
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {




        return null;
    }
}
