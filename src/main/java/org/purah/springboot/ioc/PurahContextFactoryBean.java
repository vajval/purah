package org.purah.springboot.ioc;


import org.purah.core.PurahContext;
import org.purah.core.PurahContextConfig;

import org.purah.core.matcher.FieldMatcher;

import org.purah.springboot.ann.EnablePurah;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

/**
 * 这个类负责创建整个类的 核心上下文 对象 PurahContext
 */
public class PurahContextFactoryBean implements FactoryBean<Object> {


    /**
     * 在启动时通过*****类 扫描根路径下的FieldMatcher 类
     * 将扫描到的类注册到此list
     */
    List<Class<? extends FieldMatcher>> baseStringMatcherClass;
    EnablePurah enablePurah;
    @Override
    public Object getObject() {
        PurahContextConfig purahContextConfig = new PurahContextConfig(enablePurah);
        purahContextConfig.setBaseStringMatcherClass(baseStringMatcherClass);
        return new PurahContext(purahContextConfig);
    }

    @Override
    public Class<?> getObjectType() {
        return PurahContext.class;
    }

    public List<Class<? extends FieldMatcher>> getBaseStringMatcherClass() {
        return baseStringMatcherClass;
    }

    public void setBaseStringMatcherClass(List<Class<? extends FieldMatcher>> baseStringMatcherClass) {
        this.baseStringMatcherClass = baseStringMatcherClass;
    }

    public EnablePurah getEnablePurah() {
        return enablePurah;
    }

    public void setEnablePurah(EnablePurah enablePurah) {
        this.enablePurah = enablePurah;
    }
}