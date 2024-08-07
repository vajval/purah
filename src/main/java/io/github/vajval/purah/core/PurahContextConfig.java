package io.github.vajval.purah.core;

import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.ReMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.WildCardMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.ClassNameMatcher;

import java.util.HashSet;
import java.util.Set;

public class PurahContextConfig {

    boolean cache = false;
    boolean argResolverFastInvokeCache = false;

    ResultLevel defaultResultLevel = ResultLevel.only_failed_only_base_logic;


    Set<Class<? extends FieldMatcher>> singleStringConstructorFieldMatcherClassSet = new HashSet<>();


    public PurahContextConfig() {
    }


    public Set<Class<? extends FieldMatcher>> getSingleStringConstructorFieldMatcherClassSet() {
        return singleStringConstructorFieldMatcherClassSet;
    }

    public void setSingleStringConstructorFieldMatcherClassSet(Set<Class<? extends FieldMatcher>> singleStringConstructorFieldMatcherClassSet) {
        this.singleStringConstructorFieldMatcherClassSet = singleStringConstructorFieldMatcherClassSet;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public void setDefaultResultLevel(ResultLevel defaultResultLevel) {
        this.defaultResultLevel = defaultResultLevel;
    }

    public ResultLevel getDefaultResultLevel() {
        return defaultResultLevel;
    }

    public boolean isArgResolverFastInvokeCache() {
        return argResolverFastInvokeCache;
    }

    public void setArgResolverFastInvokeCache(boolean argResolverFastInvokeCache) {
        this.argResolverFastInvokeCache = argResolverFastInvokeCache;
    }

    public Set<Class<? extends FieldMatcher>> purahDefaultFieldMatcherClass() {
        Set<Class<? extends FieldMatcher>> result = new HashSet<>();
        result.add(AnnTypeFieldMatcher.class);
        result.add(ClassNameMatcher.class);
        result.add(ReMatcher.class);
        result.add(WildCardMatcher.class);
        result.add(GeneralFieldMatcher.class);
        return result;
    }
}
