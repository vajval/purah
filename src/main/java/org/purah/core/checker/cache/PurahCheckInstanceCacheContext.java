package org.purah.core.checker.cache;

import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class PurahCheckInstanceCacheContext {

    private static final ThreadLocal<PurahCheckInstanceCacheContext> threadLocal = new ThreadLocal<>();

    private Map<InputToCheckerArgCacheKey, CheckResult> cacheMap = new ConcurrentHashMap<>();

    private int stackNum = 0;


    public static boolean isEnableOnThisThreadContext() {
        PurahCheckInstanceCacheContext purahCheckInstanceCacheContext = threadLocal.get();
        if (purahCheckInstanceCacheContext == null) return false;
        return purahCheckInstanceCacheContext.stackNum > 0;
    }


    public static void execOnCacheContext(Runnable runnable) {
        execOnCacheContext(

                () -> {
                    runnable.run();
                    return 0;
                }
        );

    }

    public static <T> T execOnCacheContext(Supplier<? extends T> supplier) {

        createEnableOnThread();
        try {
            return supplier.get();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            popCache();
        }

    }

    public synchronized static PurahCheckInstanceCacheContext createEnableOnThread() {

        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.stackNum++;
        return thisThreadContextLocalCache;
    }


    public synchronized static void popCache() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.pop();
    }

    public synchronized static void closeCache() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.stackNum = 0;
        thisThreadContextLocalCache.cacheMap.clear();
    }

    private static PurahCheckInstanceCacheContext notEnabledContext() {
        return new PurahCheckInstanceCacheContext();
    }


    public void pop() {
        this.stackNum--;
        if (stackNum == 0) {
            this.cacheMap.clear();
        }

    }


    private static PurahCheckInstanceCacheContext getCacheContextByThreadLocal() {
        PurahCheckInstanceCacheContext purahCheckInstanceCacheContext = threadLocal.get();
        if (purahCheckInstanceCacheContext == null) {
            purahCheckInstanceCacheContext = notEnabledContext();
            threadLocal.set(purahCheckInstanceCacheContext);
        }
        return purahCheckInstanceCacheContext;


    }


    public static void put(InputToCheckerArgCacheKey inputToCheckerArgCacheKey, CheckResult checkResult) {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.cacheMap.put(inputToCheckerArgCacheKey, checkResult);


    }

    public static CheckResult get(InputToCheckerArg inputToCheckerArg, String checkerName) {

        InputToCheckerArgCacheKey inputToCheckerArgCacheKey = new InputToCheckerArgCacheKey(inputToCheckerArg, checkerName);
        return get(inputToCheckerArgCacheKey);

    }

    public static CheckResult get(InputToCheckerArgCacheKey inputToCheckerArgCacheKey) {
        return getCacheContextByThreadLocal().cacheMap.get(inputToCheckerArgCacheKey);

    }


}
