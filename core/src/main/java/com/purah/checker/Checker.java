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


    default Class<?> inputCheckInstanceClass() {
        return generics()[0].resolve();
    }

    default Class<?> resultClass() {
        return generics()[1].resolve();
    }


    private ResolvableType[] generics() {
        return ResolvableType
                .forClass(this.getClass())
                .as(Checker.class)
                .getGenerics();
    }


}
