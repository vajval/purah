package org.purah.springboot.ioc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.PurahContext;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.name.NameUtil;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;
import org.purah.springboot.ioc.ann.ToChecker;
import org.purah.springboot.ioc.ann.ToCheckerFactory;
import org.purah.springboot.config.PurahConfigProperties;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * reg bean to purahContext
 */
public class PurahIocRegS {

    private static final Logger logger = LogManager.getLogger(PurahIocRegS.class);

    protected PurahContext purahContext;

    public PurahIocRegS(PurahContext purahContext) {
        this.purahContext = purahContext;
    }

    public void initMainBean(MethodConverter methodConverter, CheckerManager checkerManager, MatcherManager matcherManager, ArgResolver argResolver) {
        logger.info("init purahContext");
        if (checkerManager == null) {
            logger.info("enable  default checkerManager");
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
        for (Class<? extends FieldMatcher> baseStringMatcherClass : purahContext.config().getSingleStringConstructorFieldMatcherClassSet()) {
            this.regBaseStringMatcher(baseStringMatcherClass);
        }
        logger.info("init purahContext finish");


    }


    public void regBaseStringMatcher(Class<? extends FieldMatcher> clazz) {
        try {
            this.purahContext.matcherManager().regBaseStrMatcher(clazz);
            logger.info("reg field matcher class:{}", clazz);
        } catch (Exception e) {
            logger.error("reg field matcher error class:{}", clazz, e);
        }
    }


    public void regBaseStringMatcher(MatcherFactory matcherFactory) {
        try {
            this.purahContext.matcherManager().reg(matcherFactory);
            logger.info("reg matcher factory :{}", matcherFactory);
        } catch (Exception e) {
            logger.error("reg matcher factory error :{}", matcherFactory, e);
        }
    }


    public void regChecker(Checker<?, ?> checker) {
        try {
            purahContext.checkManager().reg(checker);
            logger.info("reg checker :{}", checker);
        } catch (Exception e) {
            logger.error("reg checker error:{}", checker, e);
        }
    }


    public void regCheckerFactory(CheckerFactory checkerFactory) {
        try {
            purahContext.checkManager().addCheckerFactory(checkerFactory);
            logger.info("reg checkerFactory :{}", checkerFactory);
        } catch (Exception e) {
            logger.error("reg checkerFactory :{}", checkerFactory, e);
        }
    }

    public void regPurahMethodsRegBean(Object enableBean) {
        if (enableBean == null) return;
        MethodConverter enableMethodConverter = purahContext.enableMethodConverter();
        List<Method> checkMethods = Stream.of(enableBean.getClass().getMethods()).filter(i -> i.getDeclaredAnnotation(ToChecker.class) != null).collect(Collectors.toList());

        List<String> checkerNameList = new ArrayList<>(checkMethods.size());
        for (Method checkMethod : checkMethods) {
            String name = NameUtil.nameByAnnOnMethod(checkMethod);
            Checker<?, ?> checker = enableMethodConverter.toChecker(enableBean, checkMethod, name);
            if (checker != null) {
                this.regChecker(checker);
                checkerNameList.add(checker.name());
            }
        }

        List<Method> checkFactoryMethods = Stream.of(enableBean.getClass().getMethods()).filter(i -> i.getDeclaredAnnotation(ToCheckerFactory.class) != null).collect(Collectors.toList());
        List<String> checkerFactroyMatchList = new ArrayList<>(checkFactoryMethods.size());

        for (Method checkMethod : checkFactoryMethods) {
            ToCheckerFactory toCheckerFactory = checkMethod.getDeclaredAnnotation(ToCheckerFactory.class);
            CheckerFactory checkerFactory = enableMethodConverter.toCheckerFactory(enableBean, checkMethod, toCheckerFactory.match(), toCheckerFactory.cacheBeCreatedChecker());
            if (checkerFactory != null) {
                this.regCheckerFactory(checkerFactory);
            }
        }

        logger.info("reg bean {} to purahContext {},{}", enableBean, checkerNameList, checkerFactroyMatchList);
    }

    public void regCheckerByProperties(PurahConfigProperties purahConfigProperties) {
        for (CombinatorialCheckerConfigProperties properties : purahConfigProperties.toCombinatorialCheckerConfigPropertiesList()) {
            try {
                Checker<?, ?> checker = purahContext.createAndRegByProperties(properties);
                logger.info("reg checker {} by properties {}", checker.name(), properties);
            } catch (Exception e) {
                logger.error("reg error by properties {}", properties, e);
            }


        }

    }

}
