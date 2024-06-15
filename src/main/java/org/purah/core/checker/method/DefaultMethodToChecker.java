package org.purah.core.checker.method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.factory.DefaultMethodToCheckerFactory;
import org.purah.core.checker.method.toChecker.AbstractMethodToChecker;
import org.purah.core.checker.method.toChecker.CheckerByAnnMethod;
import org.purah.core.checker.method.toChecker.CheckerByLogicMethod;
import org.purah.core.checker.method.toChecker.MethodToChecker;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class DefaultMethodToChecker implements MethodToChecker {
    private static final Logger logger = LogManager.getLogger(DefaultMethodToChecker.class);

    @Override
    public Checker toChecker(Object methodsToCheckersBean, Method method, String name) {

        String errorMsg = AbstractMethodToChecker.errorMsgAbstractMethodToChecker(methodsToCheckersBean, method);


        if (errorMsg != null) {
            logger.warn("{},errorMsg", method.toGenericString());

            return null;
        }
        errorMsg = CheckerByLogicMethod.errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);
        if (errorMsg == null) {
            logger.info("{}  {}", method, CheckerByLogicMethod.class);
            return new CheckerByLogicMethod(methodsToCheckersBean, method, name);
        }

        errorMsg = CheckerByAnnMethod.errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);

        if (errorMsg == null) {
            logger.info("{}  {}", method, CheckerByAnnMethod.class);
            return new CheckerByAnnMethod(methodsToCheckersBean, method, name);
        }
        logger.warn("{},没有适配的转换器", method.toGenericString());

        return null;
    }
}
