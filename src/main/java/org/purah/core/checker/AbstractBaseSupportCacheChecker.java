package org.purah.core.checker;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.cache.InputToCheckerArgCacheKey;
import org.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.LogicCheckResult;

/**
 * @param <INPUT_ARG>
 */

public abstract class AbstractBaseSupportCacheChecker<INPUT_ARG, RESULT> implements Checker<INPUT_ARG, RESULT> {
    private static final Logger logger = LogManager.getLogger(AbstractBaseSupportCacheChecker.class);


    @Override
    public CheckResult<RESULT> check(InputToCheckerArg<INPUT_ARG> inputToCheckerArg) {
        if (inputToCheckerArg == null) {
            inputToCheckerArg = InputToCheckerArg.of(null, inputArgClass());
        }
        CheckResult<RESULT> resultCheckResult = this.readCacheIfNeed(inputToCheckerArg);
        if (resultCheckResult != null) {
            return resultCheckResult;
        }

        resultCheckResult = this.doCheck(inputToCheckerArg);

        if (resultCheckResult == null) {
            logger.error("checker result is NUll logicForm " + this.logicFrom());
            throw new RuntimeException("result cannot be Null " + this.logicFrom());
        }
        setLogicFrom(resultCheckResult);

        putCacheIfNeed(inputToCheckerArg, resultCheckResult);
        return resultCheckResult;
    }


    public boolean enableCache() {
        return true;
    }

    private void putCacheIfNeed(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, CheckResult<RESULT> checkResult) {
        if (!enableCache()) {
            return;
        }
        try {
            boolean enableOnThisContext = PurahCheckInstanceCacheContext.isEnableCacheContext();
            if (!enableOnThisContext) {
                return;
            }

            InputToCheckerArgCacheKey inputToCheckerArgCacheKey = new InputToCheckerArgCacheKey(inputToCheckerArg, this.name());
            PurahCheckInstanceCacheContext.putIntoCache(inputToCheckerArgCacheKey, checkResult);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private CheckResult<RESULT> readCacheIfNeed(InputToCheckerArg<INPUT_ARG> inputToCheckerArg) {
        if (!enableCache()) {
            return null;
        }
        try {
            boolean enableOnThisContext = PurahCheckInstanceCacheContext.isEnableCacheContext();
            if (enableOnThisContext) {
                InputToCheckerArgCacheKey inputToCheckerArgCacheKey = new InputToCheckerArgCacheKey(inputToCheckerArg, this.name());
                return PurahCheckInstanceCacheContext.getResultFromCache(inputToCheckerArgCacheKey);
            }
            return null;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    Class<?> inputArgClass;
    Class<?> resultDataClass;

    @Override
    public Class<?> inputArgClass() {
        if (inputArgClass != null) return inputArgClass;
        inputArgClass = Checker.super.inputArgClass();

        return inputArgClass;
    }

    @Override
    public Class<?> resultDataClass() {
        if (resultDataClass != null) return resultDataClass;
        resultDataClass = Checker.super.resultDataClass();
        return resultDataClass;
    }

    public void setLogicFrom(CheckResult checkResult) {
        checkResult.setCheckLogicFrom(logicFrom());
    }


    protected abstract CheckResult<RESULT> doCheck(InputToCheckerArg<INPUT_ARG> inputToCheckerArg);



    public LogicCheckResult<RESULT> success(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, RESULT result) {
        return LogicCheckResult.successBuildLog(inputToCheckerArg, result);
    }

    public LogicCheckResult<RESULT> failed(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, RESULT result) {

        return LogicCheckResult.failedBuildLog(inputToCheckerArg, result);
    }

    public LogicCheckResult<RESULT> error(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, Exception e) {

        return LogicCheckResult.errorBuildLog(inputToCheckerArg, e);

    }

}
