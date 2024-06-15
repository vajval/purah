package org.purah.core.checker.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.factory.bymethod.BaseCheckerFactoryByMethod;
import org.purah.core.checker.factory.bymethod.CheckerFactoryByLogicMethod;
import org.purah.core.checker.factory.bymethod.CheckerFactoryByCheckerMethod;
import org.purah.core.checker.factory.bymethod.CheckerFactoryByPropertiesMethod;

import java.lang.reflect.Method;

public class DefaultMethodToCheckerFactory implements MethodToCheckerFactory {
    private static final Logger logger = LogManager.getLogger(DefaultMethodToCheckerFactory.class);

    @Override
    public CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker) {


        String errorMsg = BaseCheckerFactoryByMethod.errorMsgBaseCheckerFactoryByMethod(bean, method);

        if (errorMsg != null) {
            logger.warn("{},errorMsg", method.toGenericString());
            return null;
//            throw new RuntimeException(errorMsg);
        }

        errorMsg = CheckerFactoryByCheckerMethod.errorMsgCheckerFactoryByCheckerMethod(bean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, CheckerFactoryByCheckerMethod.class);
            return new CheckerFactoryByCheckerMethod(bean, method, match, cacheBeCreatedChecker);
        }


        errorMsg = CheckerFactoryByLogicMethod.errorMsgCheckerFactoryByLogicMethod(bean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, CheckerFactoryByLogicMethod.class);
            return new CheckerFactoryByLogicMethod(bean, method, match, cacheBeCreatedChecker);
        }


        errorMsg = CheckerFactoryByPropertiesMethod.errorMsgCheckerFactoryByPropertiesMethod(bean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, CheckerFactoryByPropertiesMethod.class);
            return new CheckerFactoryByPropertiesMethod(bean, method, match, cacheBeCreatedChecker);
        }

        logger.warn("{},没有适配的转换器", method.toGenericString());

        return null;


    }
}
