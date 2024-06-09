package org.purah.springboot.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.purah.core.PurahContext;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigBuilder;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.factory.bymethod.MethodToCheckerFactory;
import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;
import org.purah.core.resolver.ArgResolverManager;
import org.purah.springboot.ann.ToChecker;
import org.purah.springboot.config.PurahConfigProperties;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class RegOnContextRefresh implements ApplicationListener<ContextRefreshedEvent> {



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        PurahContext purahContext = applicationContext.getBean(PurahContext.class);

        PurahIocS purahIocS = new PurahIocS(applicationContext);

        this.initMatcherManager(purahContext, purahIocS);
        this.initArgResolverManager(purahContext, purahIocS);
        this.initCheckerManager(purahContext, purahIocS);


        this.initPurahConfigProperties(purahContext, applicationContext);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void initMatcherManager(PurahContext purahContext, PurahIocS purahIocS) {
        MatcherManager matcherManager = purahContext.matcherManager();
        Set<MatcherFactory> enableMatcherFactories = purahIocS.enableBeanSetByClass(MatcherFactory.class);
        for (MatcherFactory matcherFactory : enableMatcherFactories) {
            matcherManager.reg(matcherFactory);
        }


    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void initArgResolverManager(PurahContext purahContext, PurahIocS purahIocS) {

        ArgResolverManager argResolverManager = purahContext.argResolverManager();

        Set<ArgResolver> enableMatcherFactories = purahIocS.enableBeanSetByClass(ArgResolver.class);

        for (ArgResolver argResolver : enableMatcherFactories) {
            argResolverManager.reg(argResolver);
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
     * @param purahContext
     */

    public void initCheckerManager(PurahContext purahContext, PurahIocS purahIocS) {


        regChecker(purahContext, purahIocS);
        regCheckerFactory(purahContext, purahIocS);


    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void regChecker(PurahContext purahContext, PurahIocS purahIocS) {

        Set<Checker> checkerBeans = purahIocS.enableBeanSetByClass(Checker.class);


        List<Checker> checkersByBeanMethod = purahIocS.checkersByBeanMethod();


        CheckerManager checkerManager = purahContext.checkManager();
        for (Checker checker : checkerBeans) {
            checkerManager.reg(checker);
        }
        for (Checker checker : checkersByBeanMethod) {
            checkerManager.reg(checker);
        }

    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void regCheckerFactory(PurahContext purahContext, PurahIocS purahIocS) {

        Set<CheckerFactory> checkerFactories = purahIocS.enableBeanSetByClass(CheckerFactory.class);

        List<CheckerFactory> checkerFactoriesByBeanMethod = purahIocS.checkerFactoriesByBeanMethod();

        CheckerManager checkerManager = purahContext.checkManager();
        for (CheckerFactory checkerFactory : checkerFactories) {
            checkerManager.addCheckerFactory(checkerFactory);

        }
        for (CheckerFactory checkerFactory : checkerFactoriesByBeanMethod) {
            checkerManager.addCheckerFactory(checkerFactory);
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

        for (CombinatorialCheckerConfigBuilder properties : purahConfigProperties.toCombinatorialCheckerConfigPropertiesList()) {
            purahContext.regNewCombinatorialChecker(properties);
        }

    }

}
