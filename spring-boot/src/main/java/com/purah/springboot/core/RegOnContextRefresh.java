package com.purah.springboot.core;

import com.purah.PurahContext;
import com.purah.checker.Checker;
import com.purah.checker.CheckerManager;
import com.purah.checker.method.MethodToChecker;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.factory.CheckerFactory;
import com.purah.checker.method.SingleMethodToChecker;
import com.purah.matcher.MatcherManager;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.resolver.ArgResolver;
import com.purah.resolver.ArgResolverManager;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.springboot.ann.PurahEnableMethods;
import com.purah.springboot.config.PurahConfigProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class RegOnContextRefresh implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    PurahContext purahContext;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.initMatcherManager(purahContext.matcherManager(), applicationContext);
        this.initArgResolverManager(purahContext.argResolverManager(), applicationContext);
        this.initCheckerManager(purahContext.checkManager(), applicationContext);
        this.initPurahConfigProperties(purahContext, applicationContext);
    }





    private static <T> Set<T> filterByEnableAnn(Collection<T> inputValues) {
        Set<T> result = new HashSet<>();

        for (T t : inputValues) {
            Class<?> clazz = t.getClass();
            EnableOnPurahContext enableOnPurahContext = clazz.getDeclaredAnnotation(EnableOnPurahContext.class);
            if (enableOnPurahContext == null) {
                continue;
            }
            result.add(t);
        }
        return result;
    }

    public void initMatcherManager(MatcherManager matcherManager, ListableBeanFactory applicationContext) {

        Map<String, MatcherFactory> matcherFactoryMap = applicationContext.getBeansOfType(MatcherFactory.class);


        Set<MatcherFactory> enableMatcherFactories = filterByEnableAnn(matcherFactoryMap.values());
        for (MatcherFactory matcherFactory : enableMatcherFactories) {
            matcherManager.reg(matcherFactory);
        }


    }

    public void initArgResolverManager(ArgResolverManager argResolverManager, ListableBeanFactory applicationContext) {


        Map<String, ArgResolver> argResolverMap = applicationContext.getBeansOfType(ArgResolver.class);


        Set<ArgResolver> enableMatcherFactories = filterByEnableAnn(argResolverMap.values());
        for (ArgResolver argResolver : enableMatcherFactories) {
            argResolverManager.reg(argResolver);
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

    public void initCheckerManager(CheckerManager checkerManager, ListableBeanFactory applicationContext) {
//
        Map<String, Checker> checkerMap = applicationContext.getBeansOfType(Checker.class);


        Set<Checker> checkers = filterByEnableAnn(checkerMap.values());
        for (Checker checker : checkers) {
            checkerManager.reg(checker);
        }

        Map<String, CheckerFactory> checkerFactoryMap = applicationContext.getBeansOfType(CheckerFactory.class);



        Set<CheckerFactory> checkerFactories = filterByEnableAnn(checkerFactoryMap.values());

        for (CheckerFactory checkerFactory : checkerFactories) {
            checkerManager.addCheckerFactory(checkerFactory);
        }


        Collection<Object> values = applicationContext.getBeansWithAnnotation(PurahEnableMethods.class).values();


        Set<Object> enableMethodsToCheckers = filterByEnableAnn(values);

        for (Object bean : enableMethodsToCheckers) {
            Class<?> clazz = AopUtils.getTargetClass(bean);

            for (Method method : clazz.getMethods()) {
                if (SingleMethodToChecker.enable(bean, method)) {
                    MethodToChecker methodToChecker = new SingleMethodToChecker(bean, method);
                    checkerManager.reg(methodToChecker);
                }
            }


        }




    }
//    @Autowired
//    PurahConfigProperties purahConfigProperties;
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory applicationContext) throws BeansException {

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

}
