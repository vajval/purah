package org.purah.springboot.ioc;

import org.purah.core.PurahContext;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.checker.method.toCheckerFactory.CheckerFactoryByMethod;
import org.purah.core.checker.method.toCheckerFactory.CheckerFactoryByLogicMethod;
import org.purah.core.checker.method.toChecker.CheckerByLogicMethod;
import org.purah.core.checker.method.toCheckerFactory.MethodToCheckerFactory;
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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        PurahContext purahContext = applicationContext.getBean(PurahContext.class);


        this.initMatcherManager(purahContext, applicationContext);

        this.initArgResolverManager(purahContext, applicationContext);

        this.initCheckerManager(purahContext, applicationContext);


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

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void initMatcherManager(  PurahContext purahContext , ListableBeanFactory applicationContext) {
        MatcherManager matcherManager = purahContext.matcherManager();
        Map<String, MatcherFactory> matcherFactoryMap = applicationContext.getBeansOfType(MatcherFactory.class);

        Set<MatcherFactory> enableMatcherFactories = filterByEnableAnn(matcherFactoryMap.values());
        for (MatcherFactory matcherFactory : enableMatcherFactories) {
            matcherManager.reg(matcherFactory);
        }


    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void initArgResolverManager(   PurahContext purahContext, ListableBeanFactory applicationContext) {

        ArgResolverManager argResolverManager = purahContext.argResolverManager();
        Map<String, ArgResolver> argResolverMap = applicationContext.getBeansOfType(ArgResolver.class);


        Set<ArgResolver> enableMatcherFactories = filterByEnableAnn(argResolverMap.values());
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

    public void initCheckerManager(   PurahContext purahContext, ListableBeanFactory applicationContext) {
//


        Collection<Object> values = applicationContext.getBeansWithAnnotation(PurahEnableMethods.class).values();

        Set<Object> purahEnableMethodsBean = filterByEnableAnn(values);

        regChecker(purahContext, applicationContext, purahEnableMethodsBean);
        regCheckerFactory(purahContext, applicationContext, purahEnableMethodsBean);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void regChecker(PurahContext purahContext, ListableBeanFactory applicationContext, Set<Object> purahEnableMethodsBean) {
        Map<String, Checker> checkerMap = applicationContext.getBeansOfType(Checker.class);
        CheckerManager checkerManager = purahContext.checkManager();

        Set<Checker> checkers = filterByEnableAnn(checkerMap.values());
        for (Checker checker : checkers) {
            checkerManager.reg(checker);
        }
        Class<? extends MethodToChecker> defaultMethodToCheckerClazz = purahContext.config().getDefaultMethodToCheckerClazz();


        Map<Class<? extends MethodToChecker>, MethodToChecker> classMethodToCheckerMap = classMethodToCheckerMap(purahEnableMethodsBean, applicationContext);

        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            List<Method> methodList = Stream.of(clazz.getMethods()).filter(i -> i.getDeclaredAnnotation(ToChecker.class) != null).collect(Collectors.toList());

            for (Method method : methodList) {
                MethodToChecker methodToChecker = classMethodToCheckerMap.get(method.getDeclaredAnnotation(ToChecker.class).value());
                Checker checker = methodToChecker.toChecker(bean, method);
                checkerManager.reg(checker);
            }
        }
    }


    public Map<Class<? extends MethodToChecker>, MethodToChecker> classMethodToCheckerMap(Set<Object> purahEnableMethodsBean, ListableBeanFactory applicationContext) {
        Set<Class<? extends MethodToChecker>> allMethodToCheckerSet = new HashSet<>();
        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            Stream.of(clazz.getMethods()).map(i -> i.getDeclaredAnnotation(ToChecker.class)).filter(Objects::nonNull).map(ToChecker::value
            ).forEach(allMethodToCheckerSet::add);
        }
        allMethodToCheckerSet.remove(MethodToChecker.class);
        Map<Class<? extends MethodToChecker>, MethodToChecker> classMethodToCheckerMap = new HashMap<>();

        for (Class<? extends MethodToChecker> methodToCheckerClazz : allMethodToCheckerSet) {
            MethodToChecker bean = applicationContext.getBean(methodToCheckerClazz);
            classMethodToCheckerMap.put(methodToCheckerClazz, bean);
        }
        classMethodToCheckerMap.put(MethodToChecker.class, CheckerByLogicMethod::new);
        return classMethodToCheckerMap;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void regCheckerFactory(PurahContext purahContext, ListableBeanFactory applicationContext, Set<Object> purahEnableMethodsBean) {
        Map<String, CheckerFactory> checkerFactoryMap = applicationContext.getBeansOfType(CheckerFactory.class);
        CheckerManager checkerManager = purahContext.checkManager();

        Set<CheckerFactory> checkerFactories = filterByEnableAnn(checkerFactoryMap.values());

        for (CheckerFactory checkerFactory : checkerFactories) {
            checkerManager.addCheckerFactory(checkerFactory);
        }
        Map<Class<? extends MethodToCheckerFactory>, MethodToCheckerFactory> classMethodToCheckerFactoryMap = classMethodToCheckerFactoryMap(purahEnableMethodsBean, applicationContext);

        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            List<Method> methodList = Stream.of(clazz.getMethods()).filter(i -> i.getDeclaredAnnotation(ToCheckerFactory.class) != null).collect(Collectors.toList());
            for (Method method : methodList) {

                MethodToCheckerFactory methodToCheckerFactory = classMethodToCheckerFactoryMap.get(method.getDeclaredAnnotation(ToCheckerFactory.class).value());
                CheckerFactory checkerFactory = methodToCheckerFactory.toCheckerFactory(bean, method);

                checkerManager.addCheckerFactory(checkerFactory);

            }


        }
    }

    public Map<Class<? extends MethodToCheckerFactory>, MethodToCheckerFactory> classMethodToCheckerFactoryMap(Set<Object> purahEnableMethodsBean, ListableBeanFactory applicationContext) {
        Set<Class<? extends MethodToCheckerFactory>> allMethodToCheckerSet = new HashSet<>();
        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            Stream.of(clazz.getMethods()).map(i -> i.getDeclaredAnnotation(ToCheckerFactory.class)).filter(Objects::nonNull).map(ToCheckerFactory::value
            ).forEach(allMethodToCheckerSet::add);
        }
        allMethodToCheckerSet.remove(MethodToCheckerFactory.class);
        Map<Class<? extends MethodToCheckerFactory>, MethodToCheckerFactory> classMethodToCheckerMap = new HashMap<>();

        for (Class<? extends MethodToCheckerFactory> methodToCheckerClazz : allMethodToCheckerSet) {
            MethodToCheckerFactory bean = applicationContext.getBean(methodToCheckerClazz);
            classMethodToCheckerMap.put(methodToCheckerClazz, bean);
        }
        classMethodToCheckerMap.put(MethodToCheckerFactory.class, defaultMethodToCheckerFactory);
        return classMethodToCheckerMap;
    }


    protected static final MethodToCheckerFactory defaultMethodToCheckerFactory = (bean, method) -> {
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


            return new CheckerFactoryByLogicMethod(bean, method, match);


        } else if (length == 1) {
            Parameter parameter = parameters[0];
            if (!parameter.getType().equals(String.class)) {
                throw new RuntimeException("唯一的入参必须是 string 类型，将被填充为checker名字");
            }
            if (!Checker.class.isAssignableFrom(returnType)) {
                throw new RuntimeException("返回值必须时checker");

            }

            return new CheckerFactoryByMethod(bean, method, match);

        } else {
            throw new RuntimeException();
        }


    };

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

}
