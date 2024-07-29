package io.github.vajval.purah.core.checker.converter.checker;


import io.github.vajval.purah.core.checker.AbstractBaseSupportCacheChecker;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.PurahWrapMethod;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 函数转checker
 * wrap the method into a checker.
 */
public abstract class AbstractWrapMethodToChecker extends AbstractBaseSupportCacheChecker<Object, Object> {


    protected PurahWrapMethod purahEnableMethod;

    protected String name;

    protected Method method;

    public AbstractWrapMethodToChecker(Object methodsToCheckersBean, Method method, String name) {
        String errorMsg = errorMsgAbstractMethodToChecker(methodsToCheckersBean, method, name);
        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        this.name = name;
        this.method = method;
    }


    public static String errorMsgAbstractMethodToChecker(Object methodsToCheckersBean, Method method, String name) {
        if (!StringUtils.hasText(name)) {
            return "Hmph, name  must has text!";
        }

        if (method == null) {
            return "Hmph, the method must not be null!";
        }


        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            return "if the method isn't public, it just won't work, okay? [" + method.toGenericString() + "]";
        }

        boolean isStatic = java.lang.reflect.Modifier.isStatic(method.getModifiers());

        if (!isStatic && methodsToCheckersBean == null) {
            return "When the method is non-static, the parameter `bean` must not be null. [" + method.toGenericString() + "]";
        }
        if (methodsToCheckersBean != null) {
            boolean clazzIsPublic = java.lang.reflect.Modifier.isPublic(methodsToCheckersBean.getClass().getModifiers());
            if (!clazzIsPublic) {
                return "if the bean class isn't public, it just won't work, okay? [" + method.toGenericString() + "]";
            }
        }

        Class<?> returnType = method.getReturnType();
        if (!(CheckResult.class.isAssignableFrom(returnType)) && !(boolean.class.isAssignableFrom(returnType))) {
            return "Only supports return types of CheckResult or boolean. [" + method + "]";

        }
        return null;


    }


    @Override
    public abstract CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg);

    @Override
    public String name() {
        return name;
    }


    @Override
    public Class<?> inputArgClass() {
        return purahEnableMethod.needCheckArgClass();
    }

    @Override
    public Class<?> resultDataClass() {
        return purahEnableMethod.resultDataClass();
    }


    @Override
    public String logicFrom() {
        return purahEnableMethod.logicFrom();
    }


}