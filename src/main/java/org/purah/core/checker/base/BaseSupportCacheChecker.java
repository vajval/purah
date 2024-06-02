package org.purah.core.checker.base;


import org.purah.core.checker.cache.InstanceCheckCacheKey;
import org.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;

/**
 * @param <CHECK_INSTANCE>
 */

public abstract class BaseSupportCacheChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    @Override
    public CheckResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        CheckResult<RESULT> resultCheckResult = this.readCacheIfNeed(checkInstance);
        if (resultCheckResult != null) {
            return resultCheckResult;
        }

        resultCheckResult = this.doCheck(checkInstance);

        if (resultCheckResult == null) {
            throw new RuntimeException("result cannot be Null " + this.logicFrom());
        }


        setLogicFrom(resultCheckResult);

        putCacheIfNeed(checkInstance, resultCheckResult);
        return resultCheckResult;
    }


    public boolean enableCache() {
        return true;
    }

    private void putCacheIfNeed(CheckInstance<CHECK_INSTANCE> checkInstance, CheckResult<RESULT> checkResult) {
        if (!enableCache()) {
            return;
        }
        try {
            boolean enableOnThisContext = PurahCheckInstanceCacheContext.isEnableOnThisThreadContext();
            if (!enableOnThisContext) {
                return;
            }

            InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(checkInstance, this.name());
            PurahCheckInstanceCacheContext.put(instanceCheckCacheKey, checkResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CheckResult<RESULT> readCacheIfNeed(CheckInstance<CHECK_INSTANCE> checkInstance) {
        if (!enableCache()) {
            return null;
        }
        try {
            boolean enableOnThisContext = PurahCheckInstanceCacheContext.isEnableOnThisThreadContext();
            if (enableOnThisContext) {
                InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(checkInstance, this.name());
                return PurahCheckInstanceCacheContext.get(instanceCheckCacheKey);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setLogicFrom(CheckResult checkResult) {
        checkResult.setCheckLogicFrom(logicFrom());
    }


    public abstract CheckResult<RESULT> doCheck(CheckInstance<CHECK_INSTANCE> checkInstance);

    private String logStr(CheckInstance<CHECK_INSTANCE> checkInstance, String pre) {

        String clazzStr = checkInstance.instanceClass().getName();

        return pre + " (field [" + checkInstance.fieldStr() + "] type [" + clazzStr + "]" + ")";
    }


    public CheckResult<RESULT> success(CheckInstance<CHECK_INSTANCE> checkInstance, RESULT result) {
        String log = logStr(checkInstance, DEFAULT_SUCCESS_INFO);
        return BaseLogicCheckResult.success(result, log);
    }

    public BaseLogicCheckResult<RESULT> failed(CheckInstance<CHECK_INSTANCE> checkInstance, RESULT result) {
        String log = logStr(checkInstance, DEFAULT_FAILED_INFO);

        return BaseLogicCheckResult.failed(result, log);
    }

    public CheckResult<RESULT> error(CheckInstance<CHECK_INSTANCE> checkInstance, Exception e) {
        String log = logStr(checkInstance, DEFAULT_ERROR_INFO);
        return BaseLogicCheckResult.error(e, log);

    }

}
