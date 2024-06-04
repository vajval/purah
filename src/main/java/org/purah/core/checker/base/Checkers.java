package org.purah.core.checker.base;


import org.purah.core.base.NameUtil;
import org.purah.core.checker.method.DefaultMethodToChecker;
import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.core.checker.factory.DefaultMethodToCheckerFactory;
import org.purah.core.checker.factory.bymethod.MethodToCheckerFactory;
import org.purah.core.checker.result.CheckResult;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class Checkers {
    public static final MethodToChecker defaultMethodToChecker = new DefaultMethodToChecker();
    public static final MethodToCheckerFactory defaultMethodToCheckerFactory = new DefaultMethodToCheckerFactory();



    public abstract class CheckerFunction implements Checker {

        String name;

        public CheckerFunction(String name) {
            this.name = name;
        }

        public abstract CheckResult check(CheckInstance checkInstance);
    }





//    public static Checker checkerByStaticMethod(CheckerFunction checkerFunction) {
//
//        return checkerByStaticMethod(checkerFunction, defaultMethodToChecker);
//    }

    public static Checker checkerByStaticMethod(Method method) {
        return checkerByStaticMethod(method, defaultMethodToChecker);
    }

    public static Checker checkerByStaticMethod(Method method, MethodToChecker methodToChecker) {

        return checkerByStaticMethod(method, NameUtil.nameByAnnOnMethod(method), methodToChecker);
    }

    public static Checker checkerByStaticMethod(Method method, String name, MethodToChecker methodToChecker) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (!isStatic) {
            throw new RuntimeException();
        }
        return checkerByMethod(null, method, name, methodToChecker);
    }

    public static Checker checkerByMethod(Object methodsToCheckersBean, Method method) {

        return checkerByMethod(methodsToCheckersBean, method, defaultMethodToChecker);
    }

    public static Checker checkerByMethod(Object methodsToCheckersBean, Method method, MethodToChecker methodToChecker) {
        return checkerByMethod(methodsToCheckersBean, method, NameUtil.nameByAnnOnMethod(method), methodToChecker);
    }

    public static Checker checkerByMethod(Object methodsToCheckersBean, Method method, String name, MethodToChecker methodToChecker) {
        return methodToChecker.toChecker(methodsToCheckersBean, method, name);
    }


    public static Class<?> resultDataClass(Checker<?, ?> checker) {
        Class<?> result = generics(checker)[1].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    public static Class<?> supportInputCheckInstanceClass(Checker<?, ?> checker) {
        Class<?> result = generics(checker)[0].resolve();
        if (result == null) {
            return Object.class;
        }
        return result;
    }

    public static ResolvableType[] generics(Checker<?, ?> checker) {
        return ResolvableType
                .forClass(checker.getClass())
                .as(Checker.class)
                .getGenerics();
    }


    public static <T> BaseSupportCacheChecker<T, String> autoStringChecker(String name, Predicate<T> predicate, Class<T> clazz) {
        return new BaseSupportCacheChecker() {


            @Override
            public Class<?> inputCheckInstanceClass() {
                return clazz;
            }

            @Override
            public Class<?> resultClass() {
                return String.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public CheckResult<String> doCheck(CheckInstance checkInstance) {
                boolean test;
                try {
                    test = predicate.test((T) checkInstance.instance());
                } catch (Exception e) {
                    return error(checkInstance, e);
                }
                if (test) {
                    return success(checkInstance, "success");
                }
                return failed(checkInstance, "failed");
            }
        };
    }
}

