package org.purah.core.checker;


import org.purah.core.checker.result.CheckerResult;

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

        CheckerResult result = checker.check(checkInstance);

        result.setCheckLogicFrom(this.logicFrom());
        return result;
    }

    @Override
    public String logicFrom() {
        return checker.logicFrom();
    }
}
