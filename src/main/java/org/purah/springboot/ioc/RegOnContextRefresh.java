package org.purah.springboot.ioc;

import org.purah.core.PurahContext;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;
import org.purah.springboot.IgnoreBeanOnPurahContext;
import org.purah.springboot.ioc.ann.PurahMethodsRegBean;
import org.purah.springboot.config.PurahConfigProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;


/**
 * 刷新 容器时调用
 */

@Configuration
public class RegOnContextRefresh implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();
        PurahContext purahContext;
        try {
            purahContext = applicationContext.getBean(PurahContext.class);

        } catch (Exception e) {
            return;
        }

        PurahIocRegS purahIocRegS = new PurahIocRegS(purahContext);


        this.initMain(purahIocRegS, applicationContext);

        this.initMatcherManager(purahIocRegS, applicationContext);

        this.initCheckerManager(purahIocRegS, applicationContext);



    }

    public void initMain(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {

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
        Set<MatcherFactory> enableMatcherFactories = filterEnableBean(applicationContext.getBeansOfType(MatcherFactory.class).values());
        for (MatcherFactory matcherFactory : enableMatcherFactories) {
            purahIocRegS.regBaseStringMatcher(matcherFactory);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Reg checker  and checker factory to the Spring container
     * checker
     * 1 bean impl checker
     * 2 create by properties
     * 3 transform functions annotated with `@ToChecker` within beans annotated with `@PurahMethodsRegBean`.
     * checker factory
     * 1 bean impl checkerFactory
     * 2 transform functions annotated with `@ToCheckerFactory` within beans annotated with `@PurahMethodsRegBean`.
     */

    public void initCheckerManager(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {
        Set<Checker<?, ?>> checkers = (Set) filterEnableBean(applicationContext.getBeansOfType(Checker.class).values());

        Set<CheckerFactory> checkerFactorySet = filterEnableBean(applicationContext.getBeansOfType(CheckerFactory.class).values());
        for (Checker<?, ?> checker : checkers) {
            purahIocRegS.regChecker(checker);
        }
        for (CheckerFactory checkerFactory : checkerFactorySet) {
            purahIocRegS.regCheckerFactory(checkerFactory);
        }

        Set<Object> purahMethodsRegBeanSet = filterEnableBean(applicationContext.getBeansWithAnnotation(PurahMethodsRegBean.class).values());
        for (Object purahMethodsRegBean : purahMethodsRegBeanSet) {
            purahIocRegS.regPurahMethodsRegBean(purahMethodsRegBean);
        }
        PurahConfigProperties purahConfigProperties = applicationContext.getBean(PurahConfigProperties.class);
        purahIocRegS.regCheckerByProperties(purahConfigProperties);


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
