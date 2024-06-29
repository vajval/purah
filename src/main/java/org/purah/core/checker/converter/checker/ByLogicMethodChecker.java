package org.purah.core.checker.converter.checker;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.PurahWrapMethod;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.InitCheckerException;

import java.lang.reflect.Method;
/*
 * Convert a method like this
 * See unit test
 *
 *   @Name("test")
 *   public static boolean test(Intager ) {
 *        if (testAnn == null) {
 *             return false;
 *        }
 *        return StringUtils.hasText(testAnn.value());
 *   }
 *
 *
 */

public class ByLogicMethodChecker extends AbstractWrapMethodToChecker {


    public ByLogicMethodChecker(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        String errorMsg = errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);
        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method);


    }


    public ByLogicMethodChecker(Object methodsToCheckersBean, Method method) {
        this(methodsToCheckersBean, method, NameUtil.nameByAnnOnMethod(method));
    }


    @Override
    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {
        Object[] args = new Object[1];
        args[0] = inputToCheckerArg;
        return purahEnableMethod.invokeResult(inputToCheckerArg, args);
    }


    public static String errorMsgCheckerByLogicMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 1) {
            return "Only one parameter be check["+method.toGenericString()+"]";
        }
        return null;
    }


}

