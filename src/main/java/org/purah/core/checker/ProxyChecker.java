package org.purah.core.checker;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.init.InitCheckerException;

public class ProxyChecker implements Checker<Object, Object> {
    Checker<?, ?> checker;

    String name;

    String logicFrom;

    public ProxyChecker(Checker<?, ?> checker, String name, String logicFrom) {
        if (checker == null) {
            throw new InitCheckerException("bei proxy checker cannot be null");
        }
        this.checker = checker;
        this.name = name;
        this.logicFrom = logicFrom;
    }


    public ProxyChecker(Checker<?, ?> checker) {
        this(checker, checker.name(), checker.logicFrom());
    }

    @Override
    public CheckResult<Object> check(InputToCheckerArg<Object> inputToCheckerArg) {
        CheckResult<Object> result = ((Checker) checker).check(inputToCheckerArg);
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
