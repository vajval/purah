package org.purah.core.checker.cache;

import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.result.CheckResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PurahCheckInstanceCacheContext {

    private static final ThreadLocal<PurahCheckInstanceCacheContext> threadLocal = new ThreadLocal<>();
    private boolean enable = false;

    private int value=0;

    private Map<InstanceCheckCacheKey, CheckResult> cacheMap = new ConcurrentHashMap<>();
    public static Map<InstanceCheckCacheKey, CheckResult> cacheMap(){
        return getThisThreadContextLocalCache().cacheMap;
    }


    public  static boolean isEnableOnThisContext() {
        PurahCheckInstanceCacheContext purahCheckInstanceCacheContext = threadLocal.get();
        if(purahCheckInstanceCacheContext==null)return false;
        return purahCheckInstanceCacheContext.enable;
    }



    public static PurahCheckInstanceCacheContext createEnableOnAop() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getThisThreadContextLocalCache();

        thisThreadContextLocalCache.enable = true;
        thisThreadContextLocalCache.value++;
        return thisThreadContextLocalCache;

    }

    public static void closeThisThreadContextLocalCacheIfNotNeedOnAop() {
        PurahCheckInstanceCacheContext thisThreadContextLocalCache = getThisThreadContextLocalCache();
        if(thisThreadContextLocalCache.value==0){
            threadLocal.remove();
        }

    }



    private static PurahCheckInstanceCacheContext getThisThreadContextLocalCache() {
        PurahCheckInstanceCacheContext purahCheckInstanceCacheContext = threadLocal.get();
        if (purahCheckInstanceCacheContext == null) {
            purahCheckInstanceCacheContext = new PurahCheckInstanceCacheContext();
            purahCheckInstanceCacheContext.enable = false;
            threadLocal.set(purahCheckInstanceCacheContext);
        }
        return purahCheckInstanceCacheContext;


    }


    public static void put(InstanceCheckCacheKey instanceCheckCacheKey, CheckResult checkResult) {
        getThisThreadContextLocalCache().cacheMap.put(instanceCheckCacheKey, checkResult);


    }

    public static CheckResult get(CheckInstance checkInstance, String checkerName) {
        InstanceCheckCacheKey instanceCheckCacheKey = new InstanceCheckCacheKey(checkInstance, checkerName);

        return  getThisThreadContextLocalCache().cacheMap.get(instanceCheckCacheKey);

    }




}
