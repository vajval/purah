package org.purah.core;

import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.checker.factory.MethodToCheckerFactory;
import org.purah.core.checker.result.ResultLevel;
import org.purah.springboot.ann.EnablePurah;

public class PurahContextConfig {

    boolean cache;

    Class<? extends MethodToChecker> defaultMethodToCheckerClazz = MethodToChecker.class;

    Class<? extends MethodToCheckerFactory> defaultMethodToCheckerFactoryClazz = MethodToCheckerFactory.class;

    ResultLevel defaultResultLevel = ResultLevel.failedAndIgnoreNotBaseLogic;

    public PurahContextConfig() {
    }

    public PurahContextConfig(EnablePurah enablePurah) {
        this.cache = enablePurah.enableCache();
        this.defaultMethodToCheckerClazz = enablePurah.defaultMethodToCheckerClazz();
        this.defaultMethodToCheckerFactoryClazz = enablePurah.defaultMethodToCheckerFactoryClazz();
        this.defaultResultLevel = enablePurah.defaultResultLevel();
    }

    public boolean isCache() {
        return cache;
    }

    public Class<? extends MethodToChecker> getDefaultMethodToCheckerClazz() {
        return defaultMethodToCheckerClazz;
    }

    public Class<? extends MethodToCheckerFactory> getDefaultMethodToCheckerFactoryClazz() {
        return defaultMethodToCheckerFactoryClazz;
    }

    public ResultLevel getDefaultResultLevel() {
        return defaultResultLevel;
    }
}
