package io.github.vajval.purah.core.checker.converter.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.name.NameUtil;
import io.github.vajval.purah.core.checker.PurahWrapMethod;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

import java.lang.reflect.Method;
/*
 * Convert a method like this
 * See unit test
 *
 *   @Name("hasText")
 *   public static boolean test(String text ) {
 *        return StringUtils.hasText(text);
 *   }
 *
 *
 */

public class ByLogicMethodChecker extends AbstractWrapMethodToChecker {



    public ByLogicMethodChecker(Object methodsToCheckersBean, Method method, String name,AutoNull autoNull) {
        super(methodsToCheckersBean, method, name,autoNull);
        String errorMsg = errorMsgCheckerByLogicMethod(methodsToCheckersBean, method);
        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method);


    }



    @Override
    public CheckResult<Object> methodDoCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        Object[] args = new Object[1];
        args[0] = inputToCheckerArg;
        return purahEnableMethod.invokeResult(inputToCheckerArg, args);
    }


    public static String errorMsgCheckerByLogicMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 1) {
            return "Only one parameter be check["+method.toGenericString()+"]";
        }
        Class<?> returnType = method.getReturnType();
        if (!(CheckResult.class.isAssignableFrom(returnType)) && !(boolean.class.isAssignableFrom(returnType))) {
            return "Only supports return types of CheckResult or boolean. [" + method + "]";

        }
        return null;
    }


}

