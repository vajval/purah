package org.purah.core.checker.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.Checker;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.converter.factory.AbstractByMethodCheckerFactory;
import org.purah.core.checker.converter.factory.ByCheckerMethodCheckerFactory;
import org.purah.core.checker.converter.factory.ByLogicMethodCheckerFactory;
import org.purah.core.checker.converter.factory.ByPropertiesMethodCheckerFactory;
import org.purah.core.checker.converter.checker.FValCheckerByDefaultReflectArgResolver;
import org.purah.core.checker.converter.checker.ByAnnMethodChecker;
import org.purah.core.checker.converter.checker.ByLogicMethodChecker;
import org.purah.core.checker.converter.checker.AbstractWrapMethodToChecker;

import java.lang.reflect.Method;

public class DefaultMethodConverter implements MethodConverter {
    private static final Logger logger = LogManager.getLogger(DefaultMethodConverter.class);

    protected Checker<?, ?> doToChecker(Object methodsToCheckersBean, Method method, String name) {
        return null;
    }

    protected CheckerFactory doToCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker) {
        return null;
    }


    @Override
    public Checker<?, ?> toChecker(Object methodsToCheckersBean, Method method, String name) {

        String errorMsg = AbstractWrapMethodToChecker.errorMsgAbstractMethodToChecker(methodsToCheckersBean, method,name);


        if (errorMsg != null) {
            logger.warn("{},{}", errorMsg, method.toGenericString());
            return null;
        }

        Checker<?, ?> checker = doToChecker(methodsToCheckersBean, method, name);
        if (checker != null) {
            return checker;
        }

        errorMsg = ByLogicMethodChecker.errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, ByLogicMethodChecker.class);
            return new ByLogicMethodChecker(methodsToCheckersBean, method, name);
        }

        errorMsg = ByAnnMethodChecker.errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);

        if (errorMsg == null) {
            logger.info("{}  {}", method, ByAnnMethodChecker.class);
            return new ByAnnMethodChecker(methodsToCheckersBean, method, name);
        }
        errorMsg = FValCheckerByDefaultReflectArgResolver.errorMsgAutoMethodCheckerByDefaultReflectArgResolver
                (methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, FValCheckerByDefaultReflectArgResolver.class);
            return new FValCheckerByDefaultReflectArgResolver(methodsToCheckersBean, method, name);

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
