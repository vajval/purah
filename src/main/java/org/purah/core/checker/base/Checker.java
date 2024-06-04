package org.purah.core.checker.base;


import org.purah.core.base.IName;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

public interface Checker<CHECK_INSTANCE, RESULT> extends IName {

    String DEFAULT_SUCCESS_INFO = "SUCCESS";
    String DEFAULT_FAILED_INFO = "FAILED";
    String DEFAULT_ERROR_INFO = "ERROR";

//    boolean enableCache();


    /**
     * 校验 业务 在这里面
     */

    CheckResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance);

    default CheckResult<RESULT> check(CHECK_INSTANCE checkInstance) {
        return check(CheckInstance.create(checkInstance, inputCheckInstanceClass()));
    }

    default Class<?> inputCheckInstanceClass() {
        Class<?> result = ResolvableType.forClass(this.getClass()).as(Checker.class).getGenerics()[0].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    default Class<?> resultClass() {
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
