package org.purah.core.checker;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.SingleCheckResult;

/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    @Override
    public CheckResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        CheckResult<RESULT> resultCheckResult;
        try {
            resultCheckResult = this.doCheck(checkInstance);
            if (resultCheckResult == null) {
                throw new RuntimeException("result cannot be Null " + this.logicFrom());
            }
        } catch (Exception e) {
            throw e;
//            resultCheckerResult = this.error(checkInstance, e);
        }

        setLogicFrom(resultCheckResult);
        return resultCheckResult;
    }


    public void setLogicFrom(CheckResult checkResult) {


        checkResult.setCheckLogicFrom(logicFrom());
    }


    public abstract CheckResult<RESULT> doCheck(CheckInstance<CHECK_INSTANCE> checkInstance);

    private String logStr(CheckInstance<CHECK_INSTANCE> checkInstance, String pre) {
        Class<?> clazz = this.inputCheckInstanceClass();

        if (checkInstance.instance() != null) {
            clazz = checkInstance.instance.getClass();
        }
        String clazzStr = clazz.getName();

        return pre + "  (" + "object [" + clazzStr + "] from root [" + checkInstance.fieldStr() + "]" + ")";
    }


    public CheckResult<RESULT> success(CheckInstance<CHECK_INSTANCE> checkInstance, RESULT result) {
        String log = logStr(checkInstance, DEFAULT_SUCCESS_INFO);
        SingleCheckResult<RESULT> singleCheckerResult = SingleCheckResult.success(result, log);
        return singleCheckerResult;
    }

    public SingleCheckResult<RESULT> failed(CheckInstance<CHECK_INSTANCE> checkInstance, RESULT result) {
        String log = logStr(checkInstance, DEFAULT_FAILED_INFO);
        SingleCheckResult<RESULT> singleCheckerResult = SingleCheckResult.failed(result, log);

        return singleCheckerResult;
    }

    public CheckResult<RESULT> error(CheckInstance<CHECK_INSTANCE> checkInstance, Exception e) {
        String log = logStr(checkInstance, DEFAULT_ERROR_INFO);
        SingleCheckResult<RESULT> singleCheckerResult = SingleCheckResult.error(e, log);
        return singleCheckerResult;

    }

}
