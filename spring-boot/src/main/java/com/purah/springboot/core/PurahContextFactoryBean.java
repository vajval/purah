package com.purah.springboot.core;

import com.purah.PurahContext;
import com.purah.checker.*;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.matcher.MatcherManager;
import com.purah.matcher.clazz.AnnTypeFieldMatcher;
import com.purah.matcher.clazz.ClassNameMatcher;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.singleLevel.ReMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;
import com.purah.resolver.ArgResolver;
import com.purah.resolver.ArgResolverManager;
import com.purah.springboot.ann.MethodsToCheckers;
import com.purah.springboot.config.PurahConfigProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 这个类负责创建整个类的 核心上下文 对象 PurahContext
 */
public class PurahContextFactoryBean implements FactoryBean<Object> {


    @Autowired
    ApplicationContext applicationContext;

    /**
     * 使用者编写的规则校验参数
     */

    @Autowired
    PurahConfigProperties purahConfigProperties;


    /**
     * 在启动时通过*****类 扫描根路径下的FieldMatcher 类
     * 将扫描到的类注册到此list
     */
    List<Class<? extends FieldMatcher>> baseStringMatcherClass;


    @Override
    public Object getObject() {

        PurahContext purahContext = new PurahContext();

        this.initMatcherManager(purahContext.matcherManager());
        this.initArgResolverManager(purahContext.argResolverManager());
        this.initCheckerManager(purahContext.checkManager());
        this.initPurahConfigProperties(purahContext);

        return purahContext;

    }


    public void initMatcherManager(MatcherManager matcherManager) {

        Map<String, MatcherFactory> matcherFactoryMap = applicationContext.getBeansOfType(MatcherFactory.class);
        for (MatcherFactory matcherFactory : matcherFactoryMap.values()) {
            matcherManager.reg(matcherFactory);
        }
        matcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        matcherManager.regBaseStrMatcher(ClassNameMatcher.class);
        matcherManager.regBaseStrMatcher(ReMatcher.class);
        matcherManager.regBaseStrMatcher(WildCardMatcher.class);
        baseStringMatcherClass.forEach(matcherManager::regBaseStrMatcher);


    }

    public void initArgResolverManager(ArgResolverManager argResolverManager) {
        Map<String, ArgResolver> argResolverMap = applicationContext.getBeansOfType(ArgResolver.class);
        for (Map.Entry<String, ArgResolver> entry : argResolverMap.entrySet()) {
            argResolverManager.reg(entry.getValue());
        }

    }


    /**
     * 1 从spring 容器中找到 Checker bean 并且注册
     * 2 从spring 容器中找到 被 MethodsToCheckers 类注册的对象，并且将其中的函数构造为checker注册
     * 对于函数有两个要求
     * （1）其中 方法类必须 只能有一个入参，这唯一的入参便是检查对象
     * （2）返回结果只能是 CheckerResult Boolean  boolean 三种
     *
     * @param checkerManager
     */

    public void initCheckerManager(CheckerManager checkerManager) {

        Map<String, Checker> checkerMap = applicationContext.getBeansOfType(Checker.class);

        checkerMap.values().forEach(checkerManager::reg);


        Collection<Object> methodsToCheckersBeans = applicationContext.getBeansWithAnnotation(MethodsToCheckers.class).values();

        for (Object bean : methodsToCheckersBeans) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            for (Method method : clazz.getMethods()) {
                if (MethodChecker.enable(bean, method)) {
                    MethodChecker methodChecker = new MethodChecker(bean, method);
                    checkerManager.reg(methodChecker);
                }
            }


        }


    }


    /**
     * 根据配置文件生成 CombinatorialChecker 并且注册注
     *
     * @param purahContext
     */
    public void initPurahConfigProperties(PurahContext purahContext) {
        for (CombinatorialCheckerConfigProperties properties : purahConfigProperties.toCombinatorialCheckerConfigPropertiesList()) {
            purahContext.regNewCombinatorialChecker(properties);
        }

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