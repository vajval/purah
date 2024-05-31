package org.purah.core.checker.method.toChecker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class PurahEnableMethodValidator {
    List<Class<? extends Annotation>> annClassList;

    List<Class<?>> argList;

    List<Class<?>> allowReturnType;




    public PurahEnableMethodValidator(List<Class<? extends Annotation>> annClassList, List<Class<?>> argList,  List<Class<?>> allowReturnType) {
        this.argList = argList;
        this.annClassList = annClassList;
        this.allowReturnType = allowReturnType;
    }

    public boolean enable(Object bean, Method method) {
        return errorMsg(bean, method) == null;
    }

    public String errorMsg(Object bean, Method method) {
        for (Class<? extends Annotation> annClazz : annClassList) {
            Annotation annotation = method.getAnnotation(annClazz);
            if (annotation == null) {
                return "必须要给规则一个名字 请在对应method上增加 @Name注解" + method;
            }
        }

        if (method.getParameters().length != argList.size()) {
            return "入参只能有一个参数" + method;
        }

//        Validator
//
//        annClassList. for
//        Name nameAnnotation = method.getAnnotation(Name.class);
//        if (nameAnnotation == null) {
//            return "必须要给规则一个名字 请在对应method上增加 @Name注解" + method;
//        }
//        if (method.getParameters().length != 1) {
//            return "入参只能有一个参数" + method;
//        }
        Class<?> returnType = method.getReturnType();

        for (Class<?> clazz : allowReturnType) {
            if (clazz.isAssignableFrom(returnType)) {
                return null;
            }
        }
//        if (!(CheckResult.class.isAssignableFrom(returnType)) &&
//
//                !(boolean.class.isAssignableFrom(returnType))) {
//            return "返回值必须是 CheckResult  或者 boolean " + method;
//
//        }
//        return null;
        return "123";
    }
}
