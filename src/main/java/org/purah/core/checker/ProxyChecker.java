package org.purah.core.checker;


import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;

public class ProxyChecker implements Checker {
    Checker checker;

    String name;

    String logicFrom;

    public ProxyChecker(Checker checker, String name, String logicFrom) {
        if(checker==null){
            throw new RuntimeException("代理checker不能为null");
        }
        this.checker = checker;
        this.name = name;
        this.logicFrom = logicFrom;
    }


    public ProxyChecker(Checker checker) {
        this(checker, checker.name(), checker.logicFrom());
    }

    @Override
    public CheckResult check(InputToCheckerArg inputToCheckerArg) {
        CheckResult result = checker.check(inputToCheckerArg);
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
    public Class<?> inputArgClass() {
        return checker.inputArgClass();
    }


    @Override
    public Class<?> resultDataClass() {
        return checker.resultDataClass();
    }



}
