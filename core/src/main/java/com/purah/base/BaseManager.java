package com.purah.base;

import com.purah.checker.CheckerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础的管理器
 * @param <T>
 */

public abstract class BaseManager<T extends IName> {
    protected final Map<String, T> cacheMap = new ConcurrentHashMap<>();

    public T reg(T t) {
        return cacheMap.put(t.name(), t);
    }

    public T get(String name) {
        return cacheMap.get(name);
    }

}
