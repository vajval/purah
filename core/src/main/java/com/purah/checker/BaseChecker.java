package com.purah.checker;

import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;


/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    @Override
    public CheckerResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        CheckerResult<RESULT> resultCheckerResult;
        try {
            resultCheckerResult = this.doCheck(checkInstance);
        } catch (Exception e) {
            resultCheckerResult = this.error(e);
        }
        setLogicFrom(resultCheckerResult);
        return resultCheckerResult;
    }


    public void setLogicFrom(CheckerResult checkerResult) {
        checkerResult.setLogicFromByChecker(logicFrom());
    }



    public abstract CheckerResult<RESULT> doCheck(CheckInstance<CHECK_INSTANCE> checkInstance);


    public CheckerResult<RESULT> success(RESULT result) {

        SingleCheckerResult<RESULT> singleCheckerResult = SingleCheckerResult.success(result, DEFAULT_SUCCESS_INFO);
        return singleCheckerResult;
    }

    public SingleCheckerResult<RESULT> failed(RESULT result) {

        SingleCheckerResult<RESULT> singleCheckerResult = SingleCheckerResult.failed(result, DEFAULT_FAILED_INFO);

        return singleCheckerResult;
    }

    public CheckerResult<RESULT> error(Exception e) {

        SingleCheckerResult<RESULT> singleCheckerResult = SingleCheckerResult.error(e, DEFAULT_ERROR_INFO);
        return singleCheckerResult;

    }

}
