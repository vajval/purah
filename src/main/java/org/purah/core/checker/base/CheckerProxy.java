package org.purah.core.checker.base;


import org.purah.core.checker.result.CheckResult;

public class CheckerProxy implements Checker {
    Checker checker;

    String name;

    String logicFrom;

    public CheckerProxy(Checker checker, String name, String logicFrom) {
        if(checker==null){
            throw new RuntimeException("代理checker不能为null");
        }
        this.checker = checker;
        this.name = name;
        this.logicFrom = logicFrom;
    }


    public CheckerProxy(Checker checker) {
        this(checker, checker.name(), checker.logicFrom());
    }

    @Override
    public CheckResult check(InputCheckArg inputCheckArg) {
        CheckResult result = checker.check(inputCheckArg);
        result.setCheckLogicFrom(this.logicFrom());
        return result;
    }


    @Override
    public String logicFrom() {
        return logicFrom;
    }

    @Override
    public String name() {
        return name;
    }


    @Override
    public Class<?> inputCheckInstanceClass() {
        return checker.inputCheckInstanceClass();
    }


    @Override
    public Class<?> resultClass() {
        return checker.resultClass();
    }



}
