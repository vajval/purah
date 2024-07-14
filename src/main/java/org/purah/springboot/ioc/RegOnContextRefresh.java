package org.purah.springboot.ioc;

import org.purah.core.PurahContext;
import org.purah.core.PurahContextConfig;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;
import org.purah.springboot.ann.IgnoreBeanOnPurahContext;
import org.purah.springboot.ann.PurahMethodsRegBean;
import org.purah.springboot.config.PurahConfigProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import java.lang.annotation.Annotation;
import java.util.*;

@Configuration
public class RegOnContextRefresh implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();

        PurahContext purahContext = applicationContext.getBean(PurahContext.class);

        PurahIocRegS purahIocRegS = new PurahIocRegS(purahContext);


        this.init(purahIocRegS, applicationContext);

        this.initMatcherManager(purahIocRegS, applicationContext);

        this.initCheckerManager(purahIocRegS, applicationContext);

        this.initPurahConfigProperties(purahContext, applicationContext);


    }

    public void init(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {

        CheckerManager checkerManager = null;
        MatcherManager matcherManager = null;
        ArgResolver resolver = null;
        MethodConverter methodConverter = null;
        try {
            checkerManager = applicationContext.getBean(CheckerManager.class);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        try {
            matcherManager = applicationContext.getBean(MatcherManager.class);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        try {
            resolver = applicationContext.getBean(ArgResolver.class);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        try {
            methodConverter = applicationContext.getBean(MethodConverter.class);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        purahIocRegS.initMainBean(methodConverter, checkerManager, matcherManager, resolver);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void initMatcherManager(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {
        Set<MatcherFactory> enableMatcherFactories = enableBeanSetByClass(applicationContext, MatcherFactory.class);
        for (MatcherFactory matcherFactory : enableMatcherFactories) {
            purahIocRegS.regBaseStringMatcher(matcherFactory);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 1 从spring 容器中找到 Checker bean 并且注册
     * 2 从spring 容器中找到 被 MethodsToCheckers 类注册的对象，并且将其中的函数构造为checker注册
     * 对于函数有两个要求
     * （1）其中 方法类必须 只能有一个入参，这唯一的入参便是检查对象
     * （2）返回结果只能是 CheckResult Boolean  boolean 三种
     *
     * @param purahIocRegS
     */

    public void initCheckerManager(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {
        Set<Checker<?, ?>> checkers = (Set) enableBeanSetByClass(applicationContext, Checker.class);
        Set<CheckerFactory> checkerFactorySet = enableBeanSetByClass(applicationContext, CheckerFactory.class);
        for (Checker<?, ?> checker : checkers) {
            purahIocRegS.regChecker(checker);
        }
        for (CheckerFactory checkerFactory : checkerFactorySet) {
            purahIocRegS.regCheckerFactory(checkerFactory);
        }

        Set<Object> purahMethodsRegBeanSet = enableBeanSetByAnn(applicationContext, PurahMethodsRegBean.class);
        for (Object purahMethodsRegBean : purahMethodsRegBeanSet) {
            purahIocRegS.regPurahMethodsRegBean(purahMethodsRegBean);
        }

    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 根据配置文件生成 CombinatorialChecker 并且注册注
     *
     * @param purahContext
     */
    public void initPurahConfigProperties(PurahContext purahContext, ListableBeanFactory applicationContext) {
        PurahConfigProperties purahConfigProperties = applicationContext.getBean(PurahConfigProperties.class);
        for (CombinatorialCheckerConfigProperties properties : purahConfigProperties.toCombinatorialCheckerConfigPropertiesList()) {
            purahContext.regNewCombinatorialChecker(properties);
        }

    }


    public static Set<Object> enableBeanSetByAnn(ApplicationContext applicationContext, Class<? extends Annotation> annType) {
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(annType).values();
        return filterEnableBean(beans);
    }

    public static <T> Set<T> enableBeanSetByClass(ApplicationContext applicationContext, Class<T> searchBeanClazz) {
        Collection<T> beans = applicationContext.getBeansOfType(searchBeanClazz).values();
        return filterEnableBean(beans);
    }

    protected static <T> Set<T> filterEnableBean(Collection<T> beans) {
        Set<T> result = new HashSet<>();
        for (T bean : beans) {
            Class<?> beanClazz = AopUtils.getTargetClass(bean);
            IgnoreBeanOnPurahContext ignoreBeanOnPurahContext = beanClazz.getDeclaredAnnotation(IgnoreBeanOnPurahContext.class);
            if (ignoreBeanOnPurahContext != null) {
                continue;
            }
            result.add(bean);
        }
        return result;
    }

}
