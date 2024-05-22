package org.purah.core.checker;


import org.purah.core.checker.result.CheckerResult;
import org.purah.core.checker.result.SingleCheckerResult;

/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    @Override
    public CheckerResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        CheckerResult<RESULT> resultCheckerResult;
        try {
            resultCheckerResult = this.doCheck(checkInstance);
            if (resultCheckerResult == null) {
                throw new RuntimeException("result cannot be Null " + this.logicFrom());
            }
        } catch (Exception e) {
            throw e;
//            resultCheckerResult = this.error(checkInstance, e);
        }

        setLogicFrom(resultCheckerResult);
        return resultCheckerResult;
    }


    public void setLogicFrom(CheckerResult checkerResult) {


        checkerResult.setCheckLogicFrom(logicFrom());
    }


    public abstract CheckerResult<RESULT> doCheck(CheckInstance<CHECK_INSTANCE> checkInstance);

    private String logStr(CheckInstance<CHECK_INSTANCE> checkInstance, String pre) {
        Class<?> clazz = this.inputCheckInstanceClass();

        if (checkInstance.instance() != null) {
            clazz = checkInstance.instance.getClass();
        }
        String clazzStr = clazz.getName();

        return pre + "  (" + "object [" + clazzStr + "] from root [" + checkInstance.fieldStr() + "]" + ")";
    }


    public CheckerResult<RESULT> success(CheckInstance<CHECK_INSTANCE> checkInstance, RESULT result) {
        String log = logStr(checkInstance, DEFAULT_SUCCESS_INFO);
        SingleCheckerResult<RESULT> singleCheckerResult = SingleCheckerResult.success(result, log);
        return singleCheckerResult;
    }

    public SingleCheckerResult<RESULT> failed(CheckInstance<CHECK_INSTANCE> checkInstance, RESULT result) {
        String log = logStr(checkInstance, DEFAULT_FAILED_INFO);
        SingleCheckerResult<RESULT> singleCheckerResult = SingleCheckerResult.failed(result, log);

        return singleCheckerResult;
    }

    public CheckerResult<RESULT> error(CheckInstance<CHECK_INSTANCE> checkInstance, Exception e) {
        String log = logStr(checkInstance, DEFAULT_ERROR_INFO);
        SingleCheckerResult<RESULT> singleCheckerResult = SingleCheckerResult.error(e, log);
        return singleCheckerResult;

    }

}
