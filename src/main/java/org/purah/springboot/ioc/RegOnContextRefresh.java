package org.purah.springboot.ioc;

import org.purah.core.PurahContext;
import org.purah.core.base.PurahEnableMethod;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.method.MethodToChecker;
import org.purah.core.checker.method.MethodToCheckerFactory;
import org.purah.core.checker.method.MethodToCheckerFactoryByResult;
import org.purah.core.checker.method.BaseLogicMethodToChecker;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;
import org.purah.core.resolver.ArgResolverManager;
import org.purah.springboot.ann.EnableOnPurahContext;
import org.purah.springboot.ann.PurahEnableMethods;
import org.purah.springboot.ann.ToChecker;
import org.purah.springboot.ann.ToCheckerFactory;
import org.purah.springboot.config.PurahConfigProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * （2）返回结果只能是 CheckResult Boolean  boolean 三种
     *
     * @param checkerManager
     */

    public void initCheckerManager(CheckerManager checkerManager, ListableBeanFactory applicationContext) {
//


        Collection<Object> values = applicationContext.getBeansWithAnnotation(PurahEnableMethods.class).values();

        Set<Object> purahEnableMethodsBean = filterByEnableAnn(values);

        regChecker(checkerManager, applicationContext, purahEnableMethodsBean);
        regCheckerFactory(checkerManager, applicationContext, purahEnableMethodsBean);
    }

    public void regChecker(CheckerManager checkerManager, ListableBeanFactory applicationContext, Set<Object> purahEnableMethodsBean) {
        Map<String, Checker> checkerMap = applicationContext.getBeansOfType(Checker.class);


        Set<Checker> checkers = filterByEnableAnn(checkerMap.values());
        for (Checker checker : checkers) {
            checkerManager.reg(checker);
        }

        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            List<Method> methodList = Stream.of(clazz.getMethods()).filter(i -> i.getDeclaredAnnotation(ToChecker.class) != null).collect(Collectors.toList());

            for (Method method : methodList) {
                MethodToChecker methodToChecker = new BaseLogicMethodToChecker(bean, method);
                checkerManager.reg(methodToChecker);

            }


        }
    }

    public void regCheckerFactory(CheckerManager checkerManager, ListableBeanFactory applicationContext, Set<Object> purahEnableMethodsBean) {
        Map<String, CheckerFactory> checkerFactoryMap = applicationContext.getBeansOfType(CheckerFactory.class);


        Set<CheckerFactory> checkerFactories = filterByEnableAnn(checkerFactoryMap.values());

        for (CheckerFactory checkerFactory : checkerFactories) {
            checkerManager.addCheckerFactory(checkerFactory);
        }

        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            List<Method> methodList = Stream.of(clazz.getMethods()).filter(i -> i.getDeclaredAnnotation(ToCheckerFactory.class) != null).collect(Collectors.toList());
            for (Method method : methodList) {


                checkerManager.addCheckerFactory(methodToCheckerFactory(bean, method));

            }


        }
    }

    protected static CheckerFactory methodToCheckerFactory(Object bean, Method method) {
        ToCheckerFactory toCheckerFactory = method.getDeclaredAnnotation(ToCheckerFactory.class);
        String match = toCheckerFactory.match();
        int length = method.getParameters().length;
        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();


        if (length == 2) {
            Parameter parameter1 = parameters[0];
            if (!parameter1.getType().equals(String.class)) {
                throw new RuntimeException("第一个入参必须是 string 类型，将被填充为checker名字");
            }
            boolean valid = PurahEnableMethod.validReturnType(returnType);
            if (!valid) {
                throw new RuntimeException("返回必须是 Boolean.class 或者 CheckResult.class");
            }


            return new MethodToCheckerFactoryByResult(bean, method, match);


        } else if (length == 1) {
            Parameter parameter = parameters[0];
            if (!parameter.getType().equals(String.class)) {
                throw new RuntimeException("唯一的入参必须是 string 类型，将被填充为checker名字");
            }
            if (!Checker.class.isAssignableFrom(returnType)) {
                throw new RuntimeException("返回值必须时checker");

            }

            return new MethodToCheckerFactory(bean, method, match);

        } else {
            throw new RuntimeException();
        }
    }


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
