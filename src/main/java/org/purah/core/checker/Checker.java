package org.purah.core.checker;


import org.purah.core.base.IName;
import org.purah.core.checker.result.CheckerResult;
import org.springframework.core.ResolvableType;

public interface Checker<CHECK_INSTANCE, RESULT> extends IName {

    String DEFAULT_SUCCESS_INFO = "SUCCESS";
    String DEFAULT_FAILED_INFO = "FAILED";
    String DEFAULT_ERROR_INFO = "ERROR";


    /**
     * 校验 业务 在这里面
     */

    CheckerResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance);

    default boolean booleanCheck(CheckInstance<CHECK_INSTANCE> checkInstance) {
        return check(checkInstance).isSuccess();
    }

    default Class<?> inputCheckInstanceClass() {
        Class<?> result = generics()[0].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    default Class<?> resultClass() {
        Class<?> result = generics()[1].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }


    default ResolvableType[] generics() {
        return ResolvableType
                .forClass(this.getClass())
                .as(Checker.class)
                .getGenerics();
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
