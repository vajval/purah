package org.purah.springboot.ioc;


import org.purah.core.PurahContext;
import org.purah.core.PurahContextConfig;

import org.purah.core.matcher.FieldMatcher;

import org.purah.springboot.EnablePurah;
import org.springframework.beans.factory.FactoryBean;

import java.util.Set;

/**
 * by @EnablePurah
 * `import ImportPurahRegistrar` in the `EnablePurah` annotation.
 */
public class PurahContextFactoryBean implements FactoryBean<Object> {


    /**
     * all fieldMatcher classes under the root directory of the startup class
     */
    protected Set<Class<? extends FieldMatcher>> singleStringConstructorFieldMatcherClassSet;
    protected EnablePurah enablePurah;

    @Override
    public Object getObject() {
        PurahContextConfig purahContextConfig = new PurahContextConfig();
        purahContextConfig.setCache(enablePurah.enableCache());
        purahContextConfig.setDefaultResultLevel(enablePurah.defaultResultLevel());
        purahContextConfig.setSingleStringConstructorFieldMatcherClassSet(singleStringConstructorFieldMatcherClassSet);
        return new PurahContext(purahContextConfig);
    }

    @Override
    public Class<?> getObjectType() {
        return PurahContext.class;
    }

    public Set<Class<? extends FieldMatcher>> getSingleStringConstructorFieldMatcherClassSet() {
        return singleStringConstructorFieldMatcherClassSet;
    }

    public void setSingleStringConstructorFieldMatcherClassSet(Set<Class<? extends FieldMatcher>> singleStringConstructorFieldMatcherClassSet) {
        this.singleStringConstructorFieldMatcherClassSet = singleStringConstructorFieldMatcherClassSet;
    }

    public EnablePurah getEnablePurah() {
        return enablePurah;
    }

    public void setEnablePurah(EnablePurah enablePurah) {
        this.enablePurah = enablePurah;
    }
}