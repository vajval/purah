package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.matcher.intf.FieldMatcher;
import org.springframework.core.ResolvableType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 基础的 解析器 自带一些缓存功能
 *
 * @param <INSTANCE>
 */
public abstract class BaseArgResolver<INSTANCE> implements ArgResolver<INSTANCE> {
    Set<Class<?>> supportTypesCache=new HashSet<>(0);

    Set<Class<?>> unSupportTypesCache=new HashSet<>(0);

    public BaseArgResolver() {
        initSupport();
    }

    @Override
    public abstract Map<String, CheckInstance> getMatchFieldObjectMap(INSTANCE instance, FieldMatcher fieldMatcher);


    protected void initSupport() {
        supportTypesCache = new CopyOnWriteArraySet<>();
        unSupportTypesCache = new CopyOnWriteArraySet<>();
        ResolvableType resolvableType = ResolvableType.forType(this.getClass()).as(ArgResolver.class).getGenerics()[0];
        supportTypesCache.add(resolvableType.resolve());
    }

    @Override
    public Set<Class<?>> supportTypes() {
        if (supportTypesCache != null) return supportTypesCache;
        ResolvableType resolvableType = ResolvableType.forType(this.getClass()).as(ArgResolver.class).getGenerics()[0];
        supportTypesCache = Sets.newHashSet(resolvableType.resolve());
        return supportTypesCache;
    }

    /**
     * 大多数support测试都是通过的
     * 所以专门放同一个 set
     *
     * @param clazz 测试class
     * @return 是否支持
     */
    @Override
    public boolean support(Class<?> clazz) {
        if (supportTypesCache.contains(clazz)) return true;
        if (unSupportTypesCache.contains(clazz)) return false;

        boolean support = ArgResolver.super.support(clazz);
        if (support) {
            supportTypesCache.add(clazz);
        } else {
            unSupportTypesCache.add(clazz);
        }
        return support;
    }



}
