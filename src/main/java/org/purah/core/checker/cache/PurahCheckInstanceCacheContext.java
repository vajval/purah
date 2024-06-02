package org.purah.core.checker.cache;

import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.result.CheckResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PurahCheckInstanceCacheContext {

    private static final ThreadLocal<PurahCheckInstanceCacheContext> threadLocal = new ThreadLocal<>();

    private Map<InstanceCheckCacheKey, CheckResult> cacheMap = new ConcurrentHashMap<>();

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
            closeCache();
        }

    }

    private static PurahCheckInstanceCacheContext createEnableOnThread() {

        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.stackNum++;
        return thisThreadContextLocalCache;
    }


    private static void closeCache() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.pop();
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


    public static void put(InstanceCheckCacheKey instanceCheckCacheKey, CheckResult checkResult) {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.cacheMap.put(instanceCheckCacheKey, checkResult);


    }

    public static CheckResult get(CheckInstance checkInstance, String checkerName) {

        InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(checkInstance, checkerName);
        return get(instanceCheckCacheKey);

    }

    public static CheckResult get(InstanceCheckCacheKey instanceCheckCacheKey) {
        return getCacheContextByThreadLocal().cacheMap.get(instanceCheckCacheKey);

    }


}
