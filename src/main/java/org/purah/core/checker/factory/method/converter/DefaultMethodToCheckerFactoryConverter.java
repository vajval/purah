package org.purah.core.checker.factory.method.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.factory.*;
import org.purah.core.checker.factory.method.AbstractByMethodCheckerFactory;
import org.purah.core.checker.factory.method.ByCheckerMethodCheckerFactory;
import org.purah.core.checker.factory.method.ByLogicMethodCheckerFactory;
import org.purah.core.checker.factory.method.ByPropertiesMethodCheckerFactory;

import java.lang.reflect.Method;

public class DefaultMethodToCheckerFactoryConverter implements MethodToCheckerFactoryConverter {
    private static final Logger logger = LogManager.getLogger(DefaultMethodToCheckerFactoryConverter.class);

    @Override
    public CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker) {


        String errorMsg = AbstractByMethodCheckerFactory.errorMsgBaseCheckerFactoryByMethod(bean, method);

        if (errorMsg != null) {
            logger.warn("{},errorMsg", method.toGenericString());
            return null;
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

        logger.warn("{},没有适配的转换器", method.toGenericString());

        return null;


    }
}
