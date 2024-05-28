package org.purah.springboot.ioc;


import org.purah.core.PurahContext;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.clazz.ClassNameMatcher;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.multilevel.GeneralMultilevelFieldMatcher;

import org.purah.core.matcher.singleLevel.ReMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;
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


    @Override
    public Object getObject() {


        PurahContext purahContext = new PurahContext();
        MatcherManager matcherManager = purahContext.matcherManager();
        matcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        matcherManager.regBaseStrMatcher(ClassNameMatcher.class);
        matcherManager.regBaseStrMatcher(ReMatcher.class);
        matcherManager.regBaseStrMatcher(WildCardMatcher.class);
        matcherManager.regBaseStrMatcher(GeneralMultilevelFieldMatcher.class);
        baseStringMatcherClass.forEach(matcherManager::regBaseStrMatcher);

        return purahContext;

    }


    public List<Class<? extends FieldMatcher>> getBaseStringMatcherClass() {
        return baseStringMatcherClass;
    }

    public void setBaseStringMatcherClass(List<Class<? extends FieldMatcher>> baseStringMatcherClass) {
        this.baseStringMatcherClass = baseStringMatcherClass;
    }


    @Override
    public Class<?> getObjectType() {
        return PurahContext.class;
    }


}