package org.purah.core.checker.method.toChecker;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

public class CheckerByAnnMethod extends AbstractMethodToChecker {

    Class<?> annClazz;


    public CheckerByAnnMethod(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        this.name = name;
        annClazz = method.getParameters()[0].getType();
        String errorMsg = errorMsgCheckerByAnnMethod(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult doCheck(InputCheckArg inputCheckArg) {
        Annotation annotation = inputCheckArg.annOnField(annClazz);
        Object[] args = new Object[2];
        args[0] = annotation;
        args[1] = purahEnableMethod.inputArgValue(inputCheckArg);
        Object result = purahEnableMethod.invoke(args);
        if (purahEnableMethod.resultIsCheckResultClass()) {
            return (CheckResult) result;
        } else if (Objects.equals(result, true)) {
            return success(inputCheckArg, true);
        } else if (Objects.equals(result, false)) {
            return failed(inputCheckArg, false);
        }
        throw new RuntimeException("不阿盖出错");


    }

    @Override
    public PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahEnableMethod(methodsToCheckersBean, method, 1);
    }

    public static String errorMsgCheckerByAnnMethod(Object methodsToCheckersBean, Method method) {
        if (method.getParameters().length != 2) {
            return "入参必須有兩個参数" + method;
        }
        Class<?> parameterizedType = method.getParameters()[0].getType();

        if (!(parameterizedType.isAnnotation())) {
            return "第一個參數必須為注解，將被填充為字段上的注解值";
        }
        return null;


    }

}
