package io.github.vajval.purah.core.checker.converter;

import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.converter.checker.*;
import io.github.vajval.purah.core.checker.converter.factory.AbstractByMethodCheckerFactory;
import io.github.vajval.purah.core.checker.converter.factory.ByCheckerMethodCheckerFactory;
import io.github.vajval.purah.core.checker.converter.factory.ByLogicMethodCheckerFactory;
import io.github.vajval.purah.core.checker.converter.factory.ByPropertiesMethodCheckerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.checker.factory.CheckerFactory;

import java.lang.reflect.Method;

public class DefaultMethodConverter implements MethodConverter {
    private static final Logger logger = LogManager.getLogger(DefaultMethodConverter.class);

    protected Checker<?, ?> doToChecker(Object methodsToCheckersBean, Method method, String name, AutoNull autoNull) {
        return null;
    }

    protected CheckerFactory doToCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker) {
        return null;
    }


    @Override
    public Checker<?, ?> toChecker(Object methodsToCheckersBean, Method method, String name, AutoNull autoNull, String failedInfo) {
        String errorMsg = AbstractWrapMethodToChecker.errorMsgAbstractMethodToChecker(methodsToCheckersBean, method, name);
        if (errorMsg != null) {
            logger.warn("{},{}", errorMsg, method.toGenericString());
            return null;
        }

        Checker<?, ?> checker = doToChecker(methodsToCheckersBean, method, name, autoNull);
        if (checker != null) {
            return checker;
        }

        errorMsg = ByLogicMethodChecker.errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByLogicMethodChecker.class);
            return new ByLogicMethodChecker(methodsToCheckersBean, method, name, autoNull, failedInfo);
        }

        errorMsg = ByAnnMethodChecker.errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByAnnMethodChecker.class);
            return new ByAnnMethodChecker(methodsToCheckersBean, method, name, autoNull, failedInfo);
        }

        errorMsg = FValMethodChecker.errorMsgAutoMethodCheckerByDefaultReflectArgResolver
                (methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, FValMethodChecker.class);
            return new FValMethodChecker(methodsToCheckersBean, method, name, autoNull, failedInfo);

        }

        errorMsg = ByBaseMethodChecker.errorMsgCheckerByBaseMethod(methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByBaseMethodChecker.class);
            return new ByBaseMethodChecker(methodsToCheckersBean, method, name, autoNull, failedInfo);
        }

        logger.warn("convert  failed not enable converter,{}", method.toGenericString());
        return null;
    }


    @Override
    public CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker) {


        String errorMsg = AbstractByMethodCheckerFactory.errorMsgBaseCheckerFactoryByMethod(bean, method);

        if (errorMsg != null) {
            logger.warn("{},{}", errorMsg, method.toGenericString());
            return null;
        }
        CheckerFactory checkerFactory = doToCheckerFactory(bean, method, match, cacheBeCreatedChecker);
        if (checkerFactory != null) {
            return checkerFactory;
        }

        errorMsg = ByCheckerMethodCheckerFactory.errorMsgCheckerFactoryByCheckerMethod(bean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByCheckerMethodCheckerFactory.class);
            return new ByCheckerMethodCheckerFactory(bean, method, match, cacheBeCreatedChecker);
        }


        errorMsg = ByLogicMethodCheckerFactory.errorMsgCheckerFactoryByLogicMethod(bean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByLogicMethodCheckerFactory.class);
            return new ByLogicMethodCheckerFactory(bean, method, match, cacheBeCreatedChecker);
        }


        errorMsg = ByPropertiesMethodCheckerFactory.errorMsgCheckerFactoryByPropertiesMethod(bean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByPropertiesMethodCheckerFactory.class);
            return new ByPropertiesMethodCheckerFactory(bean, method, match, cacheBeCreatedChecker);
        }

        logger.warn("convert  failed not enable converter,{}", method.toGenericString());

        return null;


    }
}
