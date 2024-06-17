package org.purah.core.checker.method.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.Checker;
import org.purah.core.checker.method.ByAnnMethodChecker;
import org.purah.core.checker.method.ByLogicMethodChecker;
import org.purah.core.checker.method.AbstractMethodToChecker;

import java.lang.reflect.Method;

public class DefaultMethodToCheckerConverter implements MethodToCheckerConverter {
    private static final Logger logger = LogManager.getLogger(DefaultMethodToCheckerConverter.class);

    @Override
    public Checker toChecker(Object methodsToCheckersBean, Method method, String name) {

        String errorMsg = AbstractMethodToChecker.errorMsgAbstractMethodToChecker(methodsToCheckersBean, method);


        if (errorMsg != null) {
            logger.warn("{},errorMsg", method.toGenericString());

            return null;
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
        logger.warn("{},没有适配的转换器", method.toGenericString());

        return null;
    }
}
