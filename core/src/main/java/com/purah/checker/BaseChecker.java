package com.purah.checker;

import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;


/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    @Override
    public CheckerResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        try {
            return this.doCheck(checkInstance);
        } catch (Exception e) {
            e.printStackTrace();
            return this.error(e);
        }
    }




    public abstract CheckerResult<RESULT> doCheck(CheckInstance<CHECK_INSTANCE> checkInstance);



    public CheckerResult<RESULT> success(RESULT result) {

        return SingleCheckerResult.success(result, DEFAULT_SUCCESS_INFO);

    }

    public SingleCheckerResult<RESULT> failed(RESULT result) {

        return SingleCheckerResult.failed(result, DEFAULT_FAILED_INFO);

    }

    public CheckerResult<RESULT> error(Exception e) {

        return SingleCheckerResult.error(e, DEFAULT_ERROR_INFO);

    }

}
