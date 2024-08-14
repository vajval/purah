package io.github.vajval.purah.core.checker.cache;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 * ThreadLocal 缓存
 * implement caching functionality using ThreadLocal
 */


public class PurahCheckInstanceCacheContext {

    private static final ThreadLocal<PurahCheckInstanceCacheContext> threadLocal = new ThreadLocal<>();
    private final Map<InputToCheckerArgCacheKey, CheckResult<?>> cacheMap = new ConcurrentHashMap<>();

    private int stackNum = 0;

    public static void execOnCacheContext(Runnable runnable) {
        execOnCacheContext(
                () -> {
                    runnable.run();
                    return 0;
                }
        );
    }

    public static <T> T execOnCacheContext(Supplier<? extends T> supplier) {
        enableThreadCacheContext();
        try {
            return supplier.get();
        } finally {
            popCache();
        }
    }

    public static void putIntoCache(InputToCheckerArgCacheKey inputToCheckerArgCacheKey, CheckResult checkResult) {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = buildCacheContext();
        thisThreadContextLocalCache.cacheMap.put(inputToCheckerArgCacheKey, checkResult);
    }

    public static CheckResult<?> getResultFromCache(InputToCheckerArg inputToCheckerArg, String checkerName) {
        InputToCheckerArgCacheKey inputToCheckerArgCacheKey = new InputToCheckerArgCacheKey(inputToCheckerArg, checkerName);
        return getResultFromCache(inputToCheckerArgCacheKey);
    }

    public static CheckResult getResultFromCache(InputToCheckerArgCacheKey inputToCheckerArgCacheKey) {
        return buildCacheContext().cacheMap.get(inputToCheckerArgCacheKey);
    }


    public synchronized static void enableThreadCacheContext() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = buildCacheContext();
        thisThreadContextLocalCache.stackNum++;
    }

    public static boolean isEnableCacheContext() {
        PurahCheckInstanceCacheContext purahCheckInstanceCacheContext = threadLocal.get();
        if (purahCheckInstanceCacheContext == null) return false;
        return purahCheckInstanceCacheContext.stackNum > 0;
    }

    public synchronized static void popCache() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = buildCacheContext();
        thisThreadContextLocalCache.stackNum--;
        if (thisThreadContextLocalCache.stackNum == 0) {
            thisThreadContextLocalCache.cacheMap.clear();
        }
    }

    public synchronized static void destroyCache() {
        threadLocal.remove();
    }


    private static PurahCheckInstanceCacheContext buildCacheContext() {
        PurahCheckInstanceCacheContext result = threadLocal.get();
        if (result != null) {
            return result;
        }
        result = new PurahCheckInstanceCacheContext();
        threadLocal.set(result);
        return result;
    }


}
