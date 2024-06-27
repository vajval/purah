package org.purah.core;

import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.FieldMatcher;
import org.purah.springboot.ann.EnablePurah;

import java.util.ArrayList;
import java.util.List;

public class PurahContextConfig {

    boolean cache;




    ResultLevel defaultResultLevel = ResultLevel.failedAndIgnoreNotBaseLogic;


    List<Class<? extends FieldMatcher>> baseStringMatcherClass=new ArrayList<>();

    public PurahContextConfig() {
    }

    public PurahContextConfig(EnablePurah enablePurah) {
        this.cache = enablePurah.enableCache();
        this.defaultResultLevel = enablePurah.defaultResultLevel();
    }

    public List<Class<? extends FieldMatcher>> getBaseStringMatcherClass() {
        return baseStringMatcherClass;
    }

    public void setBaseStringMatcherClass(List<Class<? extends FieldMatcher>> baseStringMatcherClass) {
        this.baseStringMatcherClass = baseStringMatcherClass;
    }

    public boolean isCache() {
        return cache;
    }



    public ResultLevel getDefaultResultLevel() {
        return defaultResultLevel;
    }
}
