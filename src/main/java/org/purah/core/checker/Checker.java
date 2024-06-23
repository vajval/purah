package org.purah.core.checker;


import org.purah.core.base.IName;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

public interface Checker<INPUT_ARG, RESULT> extends IName {

    String DEFAULT_SUCCESS_INFO = "SUCCESS";
    String DEFAULT_FAILED_INFO = "FAILED";
    String DEFAULT_ERROR_INFO = "ERROR";


    /**
     * 校验 业务 在这里面
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
        Class<? extends Checker> clazz = this.getClass();
        String clazzStr = this.getClass().getName();
        if (clazz.isAnonymousClass()) {
            clazzStr = "anonymous class from " + this.getClass().getName();
        }
        return "[" + this.name() + "] " + clazzStr;
    }


}
