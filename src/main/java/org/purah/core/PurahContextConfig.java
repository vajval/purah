package org.purah.core;

import org.purah.core.checker.method.converter.MethodToCheckerConverter;
import org.purah.core.checker.factory.method.converter.MethodToCheckerFactoryConverter;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.FieldMatcher;
import org.purah.springboot.ann.EnablePurah;

import java.util.ArrayList;
import java.util.List;

public class PurahContextConfig {

    boolean cache;

    Class<? extends MethodToCheckerConverter> defaultMethodToCheckerClazz = MethodToCheckerConverter.class;

    Class<? extends MethodToCheckerFactoryConverter> defaultMethodToCheckerFactoryClazz = MethodToCheckerFactoryConverter.class;

    ResultLevel defaultResultLevel = ResultLevel.failedAndIgnoreNotBaseLogic;


    List<Class<? extends FieldMatcher>> baseStringMatcherClass=new ArrayList<>();

    public PurahContextConfig() {
    }

    public PurahContextConfig(EnablePurah enablePurah) {
        this.cache = enablePurah.enableCache();
        this.defaultMethodToCheckerClazz = enablePurah.defaultMethodToCheckerClazz();
        this.defaultMethodToCheckerFactoryClazz = enablePurah.defaultMethodToCheckerFactoryClazz();
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

    public Class<? extends MethodToCheckerConverter> getDefaultMethodToCheckerClazz() {
        return defaultMethodToCheckerClazz;
    }

    public Class<? extends MethodToCheckerFactoryConverter> getDefaultMethodToCheckerFactoryClazz() {
        return defaultMethodToCheckerFactoryClazz;
    }

    public ResultLevel getDefaultResultLevel() {
        return defaultResultLevel;
    }
}
