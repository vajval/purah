package com.purah.checker;

import com.purah.checker.context.CheckerResult;

public class CheckerProxy implements Checker {
    Checker checker;

    public CheckerProxy(Checker checker) {
        this.checker = checker;
    }

    @Override
    public Class<?> inputCheckInstanceClass() {
        return checker.inputCheckInstanceClass();
    }

    @Override
    public String name() {
        return checker.name();
    }

    @Override
    public Class<?> resultClass() {
        return checker.resultClass();
    }

    @Override
    public CheckerResult check(CheckInstance checkInstance) {
        return checker.check(checkInstance);
    }

    @Override
    public String logicFrom() {
        return checker.logicFrom();
    }
}
