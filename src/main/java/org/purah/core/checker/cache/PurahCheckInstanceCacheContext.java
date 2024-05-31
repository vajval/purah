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

    private List<List<InstanceCheckCacheKey>> cacheKeyListStack = new CopyOnWriteArrayList<>();

    public static boolean isEnableOnThisThreadContext() {
        PurahCheckInstanceCacheContext purahCheckInstanceCacheContext = threadLocal.get();
        if (purahCheckInstanceCacheContext == null) return false;
        return purahCheckInstanceCacheContext.cacheKeyListStack.size() > 0;
    }


    public static void execOnCacheContext(Runnable runnable) {
        createEnableOnThread();
        try {
            runnable.run();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeCache();
        }

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

    public static PurahCheckInstanceCacheContext createEnableOnThread() {

        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.cacheKeyListStack.add(new ArrayList<>());
        return thisThreadContextLocalCache;
    }


    public static void closeCache() {
        System.out.println("closeCache");

        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getCacheContextByThreadLocal();
        thisThreadContextLocalCache.pop();
    }

    private static PurahCheckInstanceCacheContext notEnabledContext() {
        return new PurahCheckInstanceCacheContext();
    }


    public void pop() {
        List<InstanceCheckCacheKey> needRemoveCacheKeyList = this.cacheKeyListStack.remove(this.cacheKeyListStack.size() - 1);
        if (cacheKeyListStack.size() == 0) {
            this.cacheMap.clear();
            return;
        }
//        for (InstanceCheckCacheKey instanceCheckCacheKey : needRemoveCacheKeyList) {
//            this.cacheMap.remove(instanceCheckCacheKey);
//        }
    }


    protected List<InstanceCheckCacheKey> thisCacheKeyList() {
        if (cacheKeyListStack.size() == 0) {
            throw new RuntimeException();
        }
        return cacheKeyListStack.get(cacheKeyListStack.size() - 1);
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
        CheckResult put = thisThreadContextLocalCache.cacheMap.put(instanceCheckCacheKey, checkResult);
        if (put == null) {
            List<InstanceCheckCacheKey> instanceCheckCacheKeys = thisThreadContextLocalCache.thisCacheKeyList();
            instanceCheckCacheKeys.add(instanceCheckCacheKey);

        }


    }

    public static CheckResult get(CheckInstance checkInstance, String checkerName) {

        InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(checkInstance, checkerName);
        return get(instanceCheckCacheKey);

    }

    public static CheckResult get(InstanceCheckCacheKey instanceCheckCacheKey) {
        return getCacheContextByThreadLocal().cacheMap.get(instanceCheckCacheKey);

    }


}
