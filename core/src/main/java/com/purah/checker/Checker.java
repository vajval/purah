package com.purah.checker;


import com.purah.base.IName;
import com.purah.checker.context.CheckerResult;
import org.springframework.core.ResolvableType;

public interface Checker<CHECK_INSTANCE, RESULT> extends IName {

    String DEFAULT_SUCCESS_INFO = "success";
    String DEFAULT_FAILED_INFO = "failed";
    String DEFAULT_ERROR_INFO = "error";


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


    private ResolvableType[] generics() {
        return ResolvableType
                .forClass(this.getClass())
                .as(Checker.class)
                .getGenerics();
    }

    default String logicFrom() {
        return this.getClass().getName();
    }

}
