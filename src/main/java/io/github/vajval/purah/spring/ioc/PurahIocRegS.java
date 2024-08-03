package io.github.vajval.purah.spring.ioc;

import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.CheckerManager;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.MatcherManager;
import io.github.vajval.purah.core.matcher.factory.MatcherFactory;
import io.github.vajval.purah.core.name.NameUtil;
import io.github.vajval.purah.core.resolver.ArgResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.checker.GenericsProxyChecker;
import io.github.vajval.purah.core.checker.converter.MethodConverter;
import io.github.vajval.purah.core.checker.factory.CheckerFactory;
import io.github.vajval.purah.spring.config.PurahConfigProperties;
import io.github.vajval.purah.spring.ioc.ann.ToChecker;
import io.github.vajval.purah.spring.ioc.ann.ToCheckerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * reg bean to purahContext
 */
public class PurahIocRegS {

    private static final Logger logger = LogManager.getLogger(PurahIocRegS.class);

    public final PurahContext purahContext;
    public final Purahs purahs;

    public PurahIocRegS(PurahContext purahContext) {
        this.purahContext = purahContext;
        this.purahs = purahContext.purahs();
    }

    public void initMainBean(MethodConverter methodConverter, CheckerManager checkerManager, MatcherManager matcherManager, ArgResolver argResolver) {
        logger.info("init purahContext");
        if (checkerManager == null) {
            logger.info("enable default checkerManager");
        } else {
            logger.info("enable checkerManager:{} ", checkerManager.getClass());
        }
        if (matcherManager == null) {
            logger.info("enable default matcherManager");
        } else {
            logger.info("enable matcherManager:{} ", matcherManager.getClass());
        }
        if (argResolver == null) {
            logger.info("enable default argResolver");
        } else {
            logger.info("enable argResolver:{} ", argResolver.getClass());
        }
        if (methodConverter == null) {
            logger.info("enable default methodConverter");
        } else {
            logger.info("enable methodConverter:{} ", methodConverter.getClass());
        }
        purahContext.override(checkerManager, argResolver, matcherManager, methodConverter);




    }

    public void initFieldMatcherByScanClassSet() {
        for (Class<? extends FieldMatcher> baseStringMatcherClass : purahContext.config().getSingleStringConstructorFieldMatcherClassSet()) {
            this.regBaseStringMatcher(baseStringMatcherClass);
        }
        Set<Class<? extends FieldMatcher>> purahDefaultFieldMatcherClassSet = purahContext.config().purahDefaultFieldMatcherClass();

        for (Class<? extends FieldMatcher> purahDefaultFieldMatcherClass : purahDefaultFieldMatcherClassSet) {
            String name = NameUtil.nameByAnnOnClass(purahDefaultFieldMatcherClass);
            if (name != null) {
                MatcherFactory matcherFactory = purahContext.matcherManager().factoryOf(name);
                if (matcherFactory == null) {
                    this.regBaseStringMatcher(purahDefaultFieldMatcherClass);
                }
            }
        }

    }

    public void regBaseStringMatcher(Class<? extends FieldMatcher> clazz) {
        try {

            String name = purahs.reg(clazz).name();
            logger.info("reg matcher by clazz name: [{}]   class: {}", name, clazz);
        } catch (Exception e) {
            logger.error("reg field matcher error class:{}", clazz, e);
        }
    }


    public void regBaseStringMatcher(MatcherFactory matcherFactory) {
        try {
            this.purahs.reg(matcherFactory);
            logger.info("reg matcher factory name: [{}]     class: {}", matcherFactory.name(), matcherFactory.getClass());
        } catch (Exception e) {
            logger.error("reg matcher factory error: {}", matcherFactory, e);
        }
    }


    public void regChecker(Checker<?, ?> checker) {
        try {
            this.purahs.reg(checker);
            logger.info("reg checker name: [{}]     logic from: {}", checker.name(), checker.logicFrom());
        } catch (Exception e) {
            logger.error("reg checker error:{}", checker, e);
        }
    }


    public void regCheckerFactory(CheckerFactory checkerFactory) {
        try {
            this.purahs.reg(checkerFactory);
            logger.info("reg checkerFactory: {}", checkerFactory.name());
        } catch (Exception e) {
            logger.error("reg checkerFactory: {}", checkerFactory, e);
        }
    }


    public void regPurahMethodsRegBean(Object enableBean) {
        if (enableBean == null) return;
        logger.info("start reg bean class: {} to purahContext", enableBean.getClass());
        MethodConverter enableMethodConverter = purahContext.enableMethodConverter();
        List<Method> checkMethods = Stream.of(enableBean.getClass().getMethods()).filter(i -> i.getDeclaredAnnotation(ToChecker.class) != null).collect(Collectors.toList());

        List<String> checkerNameList = new ArrayList<>(checkMethods.size());
        List<Checker<?, ?>> checkerList = new ArrayList<>();
        for (Method checkMethod : checkMethods) {
            ToChecker toChecker = checkMethod.getDeclaredAnnotation(ToChecker.class);
            Checker<?, ?> checker = enableMethodConverter.toChecker(enableBean, checkMethod, toChecker.value(),toChecker.autoNull());
            if (checker != null) {
                checkerList.add(checker);
                checkerNameList.add(checker.name());
            } else {
                logger.warn("converter method to checker failed bean class {} method {} ", enableBean.getClass(), checkMethod);
            }
        }

        List<Method> checkFactoryMethods = Stream.of(enableBean.getClass().getMethods()).filter(i -> i.getDeclaredAnnotation(ToCheckerFactory.class) != null).collect(Collectors.toList());
        List<String> checkerFactroyMatchList = new ArrayList<>(checkFactoryMethods.size());
        List<CheckerFactory> checkerFactoryList = new ArrayList<>();

        for (Method checkMethod : checkFactoryMethods) {
            ToCheckerFactory toCheckerFactory = checkMethod.getDeclaredAnnotation(ToCheckerFactory.class);
            CheckerFactory checkerFactory = enableMethodConverter.toCheckerFactory(enableBean, checkMethod, toCheckerFactory.match(), toCheckerFactory.cacheBeCreatedChecker());
            if (checkerFactory != null) {
                checkerFactoryList.add(checkerFactory);
                checkerFactroyMatchList.add(checkerFactory.name());
            } else {
                logger.warn("converter method to checkerFactory failed bean class {} method {} ", enableBean.getClass(), checkMethod);
            }
        }
        for (Checker<?, ?> checker : checkerList) {
            this.regChecker(checker);
        }
        for (CheckerFactory checkerFactory : checkerFactoryList) {
            this.regCheckerFactory(checkerFactory);
        }
        logger.info("reg bean {} to purahContext checkers: {}, factories: {}", enableBean, checkerNameList, checkerFactroyMatchList);
    }

    public void regCheckerByProperties(PurahConfigProperties purahConfigProperties) {
        for (CombinatorialCheckerConfigProperties properties : purahConfigProperties.toCombinatorialCheckerConfigPropertiesList()) {
            try {
                GenericsProxyChecker checker = purahs.reg(properties);
                logger.info("reg checker {} by properties {}", checker.name(), properties);
            } catch (Exception e) {
                logger.error("reg error by properties {}", properties, e);
            }
        }
    }


}
