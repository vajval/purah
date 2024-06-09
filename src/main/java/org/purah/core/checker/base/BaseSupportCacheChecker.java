package org.purah.core.checker.base;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.cache.InstanceCheckCacheKey;
import org.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;

/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseSupportCacheChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {
    private static final Logger logger = LogManager.getLogger(BaseSupportCacheChecker.class);

    @Override
    public CheckResult<RESULT> check(InputCheckArg<CHECK_INSTANCE> inputCheckArg) {
        if (inputCheckArg == null) {
            inputCheckArg = InputCheckArg.create(null, inputCheckInstanceClass());
        }
        CheckResult<RESULT> resultCheckResult = this.readCacheIfNeed(inputCheckArg);
        if (resultCheckResult != null) {
            return resultCheckResult;
        }

        resultCheckResult = this.doCheck(inputCheckArg);

        if (resultCheckResult == null) {

            logger.error("checker  result is NUll logicForm " + this.logicFrom());
            throw new RuntimeException("result cannot be Null " + this.logicFrom());
        }


        setLogicFrom(resultCheckResult);

        putCacheIfNeed(inputCheckArg, resultCheckResult);
        return resultCheckResult;
    }


    public boolean enableCache() {
        return true;
    }

    private void putCacheIfNeed(InputCheckArg<CHECK_INSTANCE> inputCheckArg, CheckResult<RESULT> checkResult) {
        if (!enableCache()) {
            return;
        }
        try {
            boolean enableOnThisContext = PurahCheckInstanceCacheContext.isEnableOnThisThreadContext();
            if (!enableOnThisContext) {
                return;
            }

            InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(inputCheckArg, this.name());
            PurahCheckInstanceCacheContext.put(instanceCheckCacheKey, checkResult);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private CheckResult<RESULT> readCacheIfNeed(InputCheckArg<CHECK_INSTANCE> inputCheckArg) {
        if (!enableCache()) {
            return null;
        }
        try {
            boolean enableOnThisContext = PurahCheckInstanceCacheContext.isEnableOnThisThreadContext();
            if (enableOnThisContext) {
                InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(inputCheckArg, this.name());
                return PurahCheckInstanceCacheContext.get(instanceCheckCacheKey);
            }
            return null;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public void setLogicFrom(CheckResult checkResult) {
        checkResult.setCheckLogicFrom(logicFrom());
    }


    public abstract CheckResult<RESULT> doCheck(InputCheckArg<CHECK_INSTANCE> inputCheckArg);

    /**
     * 默认的日志信息
     */

    protected String logStr(InputCheckArg<CHECK_INSTANCE> inputCheckArg, String pre) {

        String clazzStr = inputCheckArg.inputArgClass().getName();

        return pre + " (field [" + inputCheckArg.fieldStr() + "] type [" + clazzStr + "]" + ")";
    }


    public CheckResult<RESULT> success(InputCheckArg<CHECK_INSTANCE> inputCheckArg, RESULT result) {
        String log = logStr(inputCheckArg, DEFAULT_SUCCESS_INFO);
        return BaseLogicCheckResult.success(result, log);
    }

    public BaseLogicCheckResult<RESULT> failed(InputCheckArg<CHECK_INSTANCE> inputCheckArg, RESULT result) {
        String log = logStr(inputCheckArg, DEFAULT_FAILED_INFO);

        return BaseLogicCheckResult.failed(result, log);
    }

    public CheckResult<RESULT> error(InputCheckArg<CHECK_INSTANCE> inputCheckArg, Exception e) {
        String log = logStr(inputCheckArg, DEFAULT_ERROR_INFO);
        return BaseLogicCheckResult.error(e, log);

    }

}
