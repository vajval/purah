package io.github.vajval.purah.core.checker;


import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.name.IName;
import org.springframework.core.ResolvableType;

public interface Checker<INPUT_ARG, RESULT> extends IName {


    default CheckResult<RESULT> oCheck(INPUT_ARG inputArg) {
        if (inputArg == null) {
            check(InputToCheckerArg.of(null, inputArgClass()));
        }
        return check(InputToCheckerArg.of(inputArg));
    }
    /*
     * checker 入口
     * entry point
     */

    CheckResult<RESULT> check(InputToCheckerArg<INPUT_ARG> inputToCheckerArg);

    default Class<?> inputArgClass() {
        ResolvableType[] generics = ResolvableType.forClass(this.getClass()).as(Checker.class).getGenerics();
        if (generics.length > 0) {
            Class<?> result = generics[0].resolve();
            if (result == null) {
                return Object.class;
            }
            return result;
        }
        return Object.class;
    }

    default Class<?> resultDataClass() {
        ResolvableType[] generics = ResolvableType.forClass(this.getClass()).as(Checker.class).getGenerics();
        if (generics.length > 1) {
            Class<?> result = generics[1].resolve();
            if (result == null) {
                return Object.class;
            }
            return result;
        }
        return Object.class;
    }


    default String logicFrom() {
        String clazzStr = this.getClass().getName();
        if (this.getClass().isAnonymousClass()) {
            clazzStr = "anonymous class from " + this.getClass().getName();
        }
        return clazzStr + "  [" + this.name() + "] ";
    }


}
