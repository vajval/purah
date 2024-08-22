package io.github.vajval.purah.core.checker;


import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;

public class ProxyChecker implements Checker<Object, Object> {
    Checker<?, ?> checker;

    String name;

    String logicFrom;

    public ProxyChecker(Checker<?, ?> checker, String name, String logicFrom) {
        if (checker == null) {
            throw new InitCheckerException("be proxy checker cannot be null");
        }
        this.checker = checker;
        this.name = name;
        this.logicFrom = logicFrom;
    }


    @Override
    public CheckResult<Object> check(InputToCheckerArg<Object> inputToCheckerArg) {
        return (CheckResult<Object>) ((Checker) checker).check(inputToCheckerArg);
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
