package com.purah.checker;

import com.purah.checker.context.SingleCheckerResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.function.Predicate;

import static org.springframework.core.ResolvableType.forClass;

public class Checkers {


    public static  <T>  BaseChecker<T,String> autoStringChecker(String name, Predicate<T> predicate ,Class<T> clazz){
        return new BaseChecker<>() {

            @Override
            public Class<?> inputCheckInstanceClass() {
                return clazz;
            }
            @Override
            public Class<?> resultClass() {
                return super.resultClass();
            }
            @Override
            public String name() {
                return name;
            }
            @Override
            public SingleCheckerResult<String> doCheck(CheckInstance<T> checkInstance) {
                boolean test;
                try {
                    test = predicate.test(checkInstance.instance());
                } catch (Exception e) {
                    return error(e);
                }
                if (test) {
                    return success("success");
                }
                return failed("failed");
            }
        };
    }
}

//
//public static class AutoStringChecker<T> extends BaseChecker<T, String> implements Checker<T, String> {
//
//    AutoStringChecker<T> autoStringChecker;
//    Predicate<T> predicate;
//    Class<T> inputInstanceClazz;
//    String name;
//
//
//    public AutoStringChecker(String name, Predicate<T> predicate) {
//
//
//        this.name = name;
//        this.predicate = predicate;
//        this.inputInstanceClazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//    }
//
//    @Override
//    public SingleCheckerResult<String> doCheck(CheckInstance<T> checkInstance) {
//        boolean test;
//        try {
//            test = predicate.test(checkInstance.instance());
//        } catch (Exception e) {
//            return error(e);
//        }
//        if (test) {
//            return success("success");
//        }
//        return failed("failed");
//
//    }
//
//    @Override
//    public Class<?> inputCheckInstanceClass() {
//        return inputInstanceClazz;
//    }
//
//    @Override
//    public Class<?> resultClass() {
//        return String.class;
//    }
//
//    @Override
//    public String name() {
//        return name;
//    }
//}
