package com.purah.resolver;

import com.purah.base.NameUtil;
import com.purah.exception.RegException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 解析器 管理器
 */
public class ArgResolverManager {

    boolean allEmpty = true;
    //反射解析器 当没有其他可用的的解析器时 使用这个
    ArgResolver<?> defaultResolver = new DefaultArgResolver();

    /**
     * 缓存map 有直接返回 ，注册解析器等操作会清空缓存
     */
    Map<Class<?>, ArgResolver<?>> cacheMap = new ConcurrentHashMap<>();
    /**
     * 强制指定map 有限度最高
     */
    Map<Class<?>, ArgResolver<?>> assignMap = new ConcurrentHashMap<>();
    /**
     * 注册 argResolver 时 通过 supportTypes 确定的 使用方法 有冲突时会报错
     */
    Map<Class<?>, ArgResolver<?>> regMap = new ConcurrentHashMap<>();
    /**
     * 无从已经有的regMap中寻找时发现可以通过的 如     发现HashMap 可以用 ArgResolver<Map> 解析 那么 HashMap-ArgResolver<Map>就会放入 这个Map
     */
    Map<Class<?>, ArgResolver<?>> exMap = new ConcurrentHashMap<>();


    public void assign(Class<?> clazz, ArgResolver<?> argResolver) {
        assignMap.put(clazz, argResolver);
        cacheMap.put(clazz, argResolver);
    }

    /**
     * 清除是为了防止 覆盖
     * 比如如果 一个 支持Map 的resolver 在需要解析hashMap时被放入了 exArgResolverMap key HashMap.class value resolver
     * 后来又 来了一个单独支持HashMap的resolver ，那么在解析 hashMap 时应该使用hashMapResolver
     *
     * @param argResolver 解析器
     */
    public void reg(ArgResolver<?> argResolver) {

        resolverNullCheck(argResolver);

        for (Class<?> supportType : argResolver.supportTypes()) {

            clazzNullCheck(supportType);

            if (regMap.containsKey(supportType)) {
                throw new RegException("ArgResolverManager 注册异常 已经存在支持" + supportType + "的 argResolver,继续注册会导致无法确定要使用那个，请检查所有argResolver::supportTypes方法是否存在重复");
            }
            regMap.put(supportType, argResolver);
        }
        //扩展 map清楚

        exMap.clear();
        resetCacheMap();
        updateAllEmpty();
    }

    public ArgResolver getArgResolver(Class<?> argClass) {

        return cacheMap.computeIfAbsent(argClass, this::doGetArgResolver);
    }

    /**
     * 首先查询强制指定的map 有的话就使用
     * 然后看 注册的有没有配置 有的话 就用该有的
     * 然后看扩展 配置
     *
     * @param argClass
     * @return
     */

    public ArgResolver<?> doGetArgResolver(Class<?> argClass) {
        if (allEmpty) return defaultResolver;
        clazzNullCheck(argClass);


        // 第一步 查找有没有强制指定的
        ArgResolver<?> argResolver = assignMap.get(argClass);
        if (argResolver != null) return argResolver;
        // 第二步 查找 ArgResolver 注册是 从supportTypes 直接确定的
        argResolver = regMap.get(argClass);
        if (argResolver != null) return argResolver;
        // 第二步 查找 有没有间接找到的，比如 ArgResolver<Map> 支持 map ,当hashMap解析时  由于找不到 合适的，会挨个尝试，最后发现 ArgResolver<Map> 可以解析，便将其缓存
        argResolver = exMap.get(argClass);
        if (argResolver != null) return argResolver;
        List<ArgResolver<?>> canUseArgResolverList = new ArrayList<>();
        for (Map.Entry<Class<?>, ArgResolver<?>> entry : regMap.entrySet()) {
            ArgResolver<?> resolver = entry.getValue();
            if (resolver.support(argClass)) {
                canUseArgResolverList.add(resolver);
            }
        }
        if (canUseArgResolverList.size() > 1) {
            throw new RegException("ArgResolverManager::doGetArgResolver无法确定对于class " + argClass.getName() + "要使用那个解析器，可用的有"
                    + canUseArgResolverList.stream().map(NameUtil::useName).collect(Collectors.toList())
                    + "\n请使用assign强制指定 或检查呢:supportTypes方法是否冲突"
            );
        }
        if (canUseArgResolverList.size() == 0) {
            this.regExArgResolverMapping(argClass, defaultResolver);
            return defaultResolver;
        }
        //下次查询快点
        this.regExArgResolverMapping(argClass, canUseArgResolverList.get(0));
        return canUseArgResolverList.get(0);
    }


    public void setDefaultResolver(ArgResolver<?> defaultResolver) {
        this.defaultResolver = defaultResolver;
    }


    public void regExArgResolverMapping(Class<?> clazz, ArgResolver<?> argResolver) {
        clazzNullCheck(clazz);
        resolverNullCheck(argResolver);
        if (exMap.containsKey(clazz)) {
            throw new RegException("扩展注册异常 不应该这里出现异常的");
        }
        exMap.put(clazz, argResolver);
        updateAllEmpty();
    }

    public synchronized void resetCacheMap() {
        cacheMap.clear();
        cacheMap.putAll(assignMap);
    }

    public static void clazzNullCheck(Class<?> clazz) {
        if (clazz == null) {
            throw new RegException("ArgResolverManager 注册yong 输入class 不能为 null");
        }
    }

    public static void resolverNullCheck(ArgResolver<?> argResolver) {
        if (argResolver == null) {
            throw new RegException("ArgResolverManager 注册异常 argResolver 值不能为 null");
        }
    }

    public void updateAllEmpty() {

        allEmpty = CollectionUtils.isEmpty(cacheMap) && CollectionUtils.isEmpty(assignMap) && CollectionUtils.isEmpty(regMap) && CollectionUtils.isEmpty(exMap);
    }

}
