package com.purah.checker;

import com.purah.checker.context.SingleCheckerResult;
import org.springframework.core.ResolvableType;


/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    @Override
    public SingleCheckerResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        try {
            return this.doCheck(checkInstance);
        } catch (Exception e) {
            return this.error(e);
        }
    }




    public abstract SingleCheckerResult<RESULT> doCheck(CheckInstance<CHECK_INSTANCE> checkInstance);



    public SingleCheckerResult<RESULT> success(RESULT result) {

        return SingleCheckerResult.success(result, DEFAULT_SUCCESS_INFO);

    }

    public SingleCheckerResult<RESULT> failed(RESULT result) {

        return SingleCheckerResult.failed(result, DEFAULT_FAILED_INFO);

    }

    public SingleCheckerResult<RESULT> error(Exception e) {

        return SingleCheckerResult.error(e, DEFAULT_ERROR_INFO);

    }

}
