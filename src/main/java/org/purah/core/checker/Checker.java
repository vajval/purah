package org.purah.core.checker;


import org.purah.core.name.IName;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

public interface Checker<INPUT_ARG, RESULT> extends IName {


    /**
     * checker 入口
     * entry point
     */

    CheckResult<RESULT> check(InputToCheckerArg<INPUT_ARG> inputToCheckerArg);

    default CheckResult<RESULT> check(INPUT_ARG inputArg) {
        return check(InputToCheckerArg.of(inputArg, inputArgClass()));
    }

    default Class<?> inputArgClass() {
        Class<?> result = ResolvableType.forClass(this.getClass()).as(Checker.class).getGenerics()[0].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    default Class<?> resultDataClass() {
        Class<?> result = ResolvableType.forClass(this.getClass()).as(Checker.class).getGenerics()[1].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }


    default String logicFrom() {
        String clazzStr = this.getClass().getName();
        if (this.getClass().isAnonymousClass()) {
            clazzStr = "anonymous class from " + this.getClass().getName();
        }
        return clazzStr + "  [" + this.name() + "] ";
    }


}
