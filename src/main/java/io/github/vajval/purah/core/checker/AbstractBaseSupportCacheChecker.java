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
    Info<INPUT_ARG> info;

    static class Info<INPUT_ARG> {

        protected String logicFrom;
        InputToCheckerArg<INPUT_ARG> NULL;
        Class<?> inputArgClass;
        Class<?> resultDataClass;
    }

    protected Info<INPUT_ARG> info() {
        if (info == null) {
            info = new Info<>();
            info.logicFrom = logicFrom();
            info.NULL = InputToCheckerArg.of(null, inputArgClass());
            info.inputArgClass = Checker.super.inputArgClass();
            info.resultDataClass =Checker.super.resultDataClass();

        }
        return info;
    }

    @Override
    public CheckResult<RESULT> check(InputToCheckerArg<INPUT_ARG> inputToCheckerArg) {

        if (inputToCheckerArg == null) {
            inputToCheckerArg = info().NULL;
        }
        boolean enableCache = enableCache();
        if (!enableCache) {
            CheckResult<RESULT> resultCheckResult = this.doCheck(inputToCheckerArg);
            if (resultCheckResult == null) {
                throw new RuntimeException("checker result  cannot be Null  " + this.logicFrom());
            }
            return resultCheckResult;
        }
        CheckResult<RESULT> resultCheckResult = this.readCache(inputToCheckerArg);
        if (resultCheckResult != null) {
            return resultCheckResult;
        }
        resultCheckResult = this.doCheck(inputToCheckerArg);
        if (resultCheckResult == null) {
            logger.error("checker result is NUll logicForm " + this.logicFrom());
            throw new RuntimeException("result cannot be Null " + this.logicFrom());
        }
        putCache(inputToCheckerArg, resultCheckResult);
        return resultCheckResult;
    }


    public boolean enableCache() {
        return true;
    }

    private void putCache(InputToCheckerArg<INPUT_ARG> inputToCheckerArg, CheckResult<RESULT> checkResult) {
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

    private CheckResult<RESULT> readCache(InputToCheckerArg<INPUT_ARG> inputToCheckerArg) {
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


    @Override
    public Class<?> inputArgClass() {
       return info().inputArgClass;
    }

    @Override
    public Class<?> resultDataClass() {
        return info().resultDataClass;
    }




    protected abstract CheckResult<RESULT> doCheck(InputToCheckerArg<INPUT_ARG> inputToCheckerArg);



}
