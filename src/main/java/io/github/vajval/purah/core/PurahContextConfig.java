package io.github.vajval.purah.core;

import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.nested.AnnByPackageMatcher;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.matcher.nested.NormalMultiLevelMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.*;

import java.util.HashSet;
import java.util.Set;

public class PurahContextConfig {

    boolean cache = false;
    boolean enableExtendUnsafeCache = false;

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

    public boolean isEnableExtendUnsafeCache() {
        return enableExtendUnsafeCache;
    }

    public void setEnableExtendUnsafeCache(boolean enableExtendUnsafeCache) {
        this.enableExtendUnsafeCache = enableExtendUnsafeCache;
    }

    public Set<Class<? extends FieldMatcher>> purahDefaultFieldMatcherClass() {
        Set<Class<? extends FieldMatcher>> result = new HashSet<>();
        result.add(AnnTypeFieldMatcher.class);
        result.add(ClassNameMatcher.class);
        result.add(ReMatcher.class);
        result.add(WildCardMatcher.class);
        result.add(GeneralFieldMatcher.class);

//        result.add(AnnByPackageMatcher.class);
        result.add(FixedMatcher.class);
        result.add(NormalMultiLevelMatcher.class);
        result.add(EqualMatcher.class);

        return result;
    }
}
