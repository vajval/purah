package org.purah.core;

import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.FieldMatcher;

import java.util.HashSet;
import java.util.Set;

public class PurahContextConfig {

    boolean cache;

    ResultLevel defaultResultLevel = ResultLevel.failedAndIgnoreNotBaseLogic;


    Set<Class<? extends FieldMatcher>> singleStringConstructorFieldMatcherClassSet =new HashSet<>();

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
}
