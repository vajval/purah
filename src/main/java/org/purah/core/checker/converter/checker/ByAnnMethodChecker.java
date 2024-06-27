package org.purah.core.checker.converter.checker;

import org.purah.core.checker.PurahWrapMethod;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.InitCheckerException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/*
 * Convert a method like this
 * See unit test
 *
 *   @Name("test")
 *   public static boolean test(TestAnn testAnn, String name) {
 *        if (testAnn == null) {
 *             return false;
 *        }
 *        return StringUtils.hasText(testAnn.value());
 *   }
 *
 *
 */

public class ByAnnMethodChecker extends AbstractWrapMethodToChecker {

    Class<?> annClazz;


    public ByAnnMethodChecker(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);

        String errorMsg = errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        this.name = name;
        this.annClazz = method.getParameters()[0].getType();
        this.purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method, 1);

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {
        Annotation annotation = inputToCheckerArg.annOnField(annClazz);
        Object[] args = new Object[]{annotation, inputToCheckerArg};
        return purahEnableMethod.invokeResult(inputToCheckerArg, args);
    }


    public static String errorMsgCheckerByAnnMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 2) {
            return "you need to have two parameters! [" + method + "]";
        }
        Class<?> parameterizedType = method.getParameters()[0].getType();

        if (!(parameterizedType.isAnnotation())) {
            return "Um, the first parameter has to be an annotation, and it'll be filled with the annotation value from the field [" + method + "]";
        }
        return null;


    }

}
