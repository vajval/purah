package com.purah.springboot.core;

import com.purah.PurahContext;
import com.purah.checker.*;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.MatcherManager;
import com.purah.matcher.clazz.AnnTypeFieldMatcher;
import com.purah.matcher.clazz.ClassNameMatcher;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.multilevel.GeneralMultilevelFieldMatcher;
import com.purah.matcher.singleLevel.ReMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;
import com.purah.resolver.ArgResolver;
import com.purah.resolver.ArgResolverManager;
import com.purah.springboot.ann.EnableOnPurahContext;

import com.purah.springboot.config.PurahConfigProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;

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