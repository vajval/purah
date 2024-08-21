package io.github.vajval.purah.spring.ioc.refresh;

import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.CheckerManager;
import io.github.vajval.purah.core.checker.converter.MethodConverter;
import io.github.vajval.purah.core.checker.factory.CheckerFactory;
import io.github.vajval.purah.core.matcher.MatcherManager;
import io.github.vajval.purah.core.matcher.factory.MatcherFactory;
import io.github.vajval.purah.core.resolver.ArgResolver;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.spring.IgnoreBeanOnPurahContext;
import io.github.vajval.purah.spring.config.PurahConfigProperties;
import io.github.vajval.purah.spring.extra.ExampleCustomSyntaxCheckerFactory;
import io.github.vajval.purah.spring.ioc.PurahIocRegS;
import io.github.vajval.purah.spring.ioc.ann.PurahMethodsRegBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PurahRefreshHelper {

    private static final Logger logger = LogManager.getLogger(PurahRefreshHelper.class);


    PurahContext purahContext;
    ApplicationContext applicationContext;

    public PurahRefreshHelper(PurahContext purahContext, ApplicationContext applicationContext) {
        this.purahContext = purahContext;
        this.applicationContext = applicationContext;
    }


    public void refresh() {
        purahContext.clearAll();
        PurahIocRegS purahIocRegS = new PurahIocRegS(purahContext);

        this.initMain(purahIocRegS, applicationContext);
        this.initMatcherManager(purahIocRegS, applicationContext);
        this.initCheckerManager(purahIocRegS, applicationContext);
        this.callBack(purahIocRegS, applicationContext);

        logger.info("purahContext refresh finish");
        logger.info("Ciallo～(∠・ω< )⌒★");


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
        if (resolver instanceof ReflectArgResolver) {
            boolean argResolverFastInvokeCache = purahIocRegS.purahContext.config().isArgResolverFastInvokeCache();
            ((ReflectArgResolver) resolver).configCache(argResolverFastInvokeCache);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void initMatcherManager(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {
        Set<MatcherFactory> enableMatcherFactories = filterEnableBean(applicationContext.getBeansOfType(MatcherFactory.class).values());
        for (MatcherFactory matcherFactory : enableMatcherFactories) {
            purahIocRegS.regBaseStringMatcher(matcherFactory);
        }
        purahIocRegS.initFieldMatcherByScanClassSet();
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /*
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

        try {
            PurahConfigProperties purahConfigProperties = applicationContext.getBean(PurahConfigProperties.class);
            purahIocRegS.regCheckerByProperties(purahConfigProperties);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        purahIocRegS.regCheckerFactory(new ExampleCustomSyntaxCheckerFactory(purahContext.purahs()));


    }


    public void callBack(PurahIocRegS purahIocRegS, ApplicationContext applicationContext) {
        Set<PurahRefreshCallBack> callBackSet = filterEnableBean(applicationContext.getBeansOfType(PurahRefreshCallBack.class).values());
        for (PurahRefreshCallBack purahRefreshCallBack : callBackSet) {
            purahRefreshCallBack.exec(purahIocRegS.purahs);
        }
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
