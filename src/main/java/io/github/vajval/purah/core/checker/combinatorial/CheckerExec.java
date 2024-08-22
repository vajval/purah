package io.github.vajval.purah.core.checker.combinatorial;

import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.result.CheckResult;

public class CheckerExec {
    public final Checker<?, ?> checker;
    public final InputToCheckerArg<?> inputToCheckerArg;

    public CheckerExec(Checker<?, ?> checker, InputToCheckerArg<?> inputToCheckerArg) {
        this.checker = checker;
        this.inputToCheckerArg = inputToCheckerArg;
    }

    public CheckResult<?> exec() {
        return ((Checker) checker).check(inputToCheckerArg);
    }
}
