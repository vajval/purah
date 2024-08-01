package io.github.vajval.purah.core.checker;


import io.github.vajval.purah.core.checker.cache.InputToCheckerArgCacheKey;
import io.github.vajval.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * checker的基础类,实现了缓存功能,需要缓存的话需要 enableCache()返回true,name()要唯一
 * Base class for checker, implements caching functionality. To enable caching, enableCache() should return true, and name() must be unique
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

    public void setLogicFrom(CheckResult<?> checkResult) {
        checkResult.setCheckLogicFrom(logicFrom());
    }


    protected abstract CheckResult<RESULT> doCheck(InputToCheckerArg<INPUT_ARG> inputToCheckerArg);



    protected LogicCheckResult<RESULT> success(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, RESULT result) {
        return LogicCheckResult.successBuildLog(inputToCheckerArg, result);
    }

    protected LogicCheckResult<RESULT> failed(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, RESULT result) {

        return LogicCheckResult.failedBuildLog(inputToCheckerArg, result);
    }

//    protected LogicCheckResult<RESULT> error(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, Exception e) {
//
//        return LogicCheckResult.errorBuildLog(inputToCheckerArg, e);
//
//    }

}
