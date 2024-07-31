package io.github.vajval.purah.core.checker.converter.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.checker.PurahWrapMethod;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/*
 * 函数转checker
 * Convert a method like this
 * See unit test
 *
 *   @Name("test")
 *   public static boolean test(@TestAnn testAnn, String name) {
 *        if (testAnn == null) {
 *             return false;
 *        }
 *        return StringUtils.hasText(testAnn.value());
 *   }
 *
 *
 */

public class ByAnnMethodChecker extends AbstractWrapMethodToChecker {

    Class<? extends Annotation> annClazz;


    public ByAnnMethodChecker(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);

        String errorMsg = errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        this.name = name;
        this.annClazz = (Class) method.getParameters()[0].getType();
        this.purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method, 1);

    }

    public Class<? extends Annotation> annClazz() {
        return annClazz;
    }

    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
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
        Class<?> returnType = method.getReturnType();
        if (!(CheckResult.class.isAssignableFrom(returnType)) && !(boolean.class.isAssignableFrom(returnType))) {
            return "Only supports return types of CheckResult or boolean. [" + method + "]";

        }
        return null;


    }

}
