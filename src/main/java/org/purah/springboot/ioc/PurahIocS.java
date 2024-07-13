package org.purah.springboot.ioc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.PurahContext;
import org.purah.core.name.NameUtil;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;
import org.purah.springboot.ann.convert.ToChecker;
import org.purah.springboot.ann.convert.ToCheckerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurahIocS {

    private static final Logger logger = LogManager.getLogger(PurahIocS.class);

    PurahContext purahContext;

    public PurahIocS(PurahContext purahContext) {
        this.purahContext = purahContext;
    }

    public void initMainBean(MethodConverter methodConverter, CheckerManager checkerManager, MatcherManager matcherManager, ArgResolver argResolver) {
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
    }




    public void regMatcherFactory(Class<? extends FieldMatcher> clazz) {
        try {
            this.purahContext.matcherManager().regBaseStrMatcher(clazz);
            logger.info("123");
        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void regMatcherFactory(MatcherFactory matcherFactory) {
        try {
            this.purahContext.matcherManager().reg(matcherFactory);
            logger.info("123");

        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void regChecker(Checker<?, ?> checker) {
        try {
            purahContext.checkManager().reg(checker);
            logger.info("123");

        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void regCheckerFactory(CheckerFactory checkerFactory) {
        try {
            purahContext.checkManager().addCheckerFactory(checkerFactory);
            logger.info("123");

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void regPurahMethodsRegBean(Object enableBean) {
        MethodConverter enableMethodConverter = purahContext.enableMethodConverter();
        CheckerManager checkerManager = purahContext.checkManager();

        List<Method> checkMethods = Stream.of(enableBean.getClass().getMethods()).filter(i -> i.getDeclaredAnnotation(ToChecker.class) != null).collect(Collectors.toList());

        for (Method checkMethod : checkMethods) {
            String name = NameUtil.nameByAnnOnMethod(checkMethod);
            Checker<?, ?> checker = enableMethodConverter.toChecker(enableBean, checkMethod, name);
            if (checker != null) {
                checkerManager.reg(checker);
            }
        }

        List<Method> checkFactoryMethods = Stream.of(enableBean.getClass().getMethods()).filter(i -> i.getDeclaredAnnotation(ToCheckerFactory.class) != null).collect(Collectors.toList());
        for (Method checkMethod : checkFactoryMethods) {
            ToCheckerFactory toCheckerFactory = checkMethod.getDeclaredAnnotation(ToCheckerFactory.class);
            CheckerFactory checkerFactory = enableMethodConverter.toCheckerFactory(enableBean, checkMethod, toCheckerFactory.match(), toCheckerFactory.cacheBeCreatedChecker());
            if (checkerFactory != null) {
                checkerManager.addCheckerFactory(checkerFactory);
            }
        }
    }


}
