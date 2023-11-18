//package com.purah.checker;
//
//import com.vajva.rule.RuleContext;
//
//import java.lang.reflect.Method;
//
//public class CheckerMethod {
//    Method method;
//    String name;
//    Object belongObject;
//    ArgSortType argSortType;
//
//    public CheckerMethod(Object belongObject, Method method, String name, ArgSortType argSortType) {
//        this.method = method;
//        this.name = name;
//        this.belongObject = belongObject;
//        this.argSortType = argSortType;
//    }
//
//    public CheckerResult<?> invoke(CheckInstance checkInstance, Object CheckInstance) {
//        try {
//            if (argSortType == ArgSortType.check) {
//                return (CheckerResult) method.invoke(belongObject, CheckInstance);
//            } else if (argSortType == ArgSortType.check_context) {
//                return (CheckerResult) method.invoke(belongObject, CheckInstance, checkInstance);
//            } else if (argSortType == ArgSortType.context_check) {
//                return (CheckerResult) method.invoke(belongObject, checkInstance, CheckInstance);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException();
//
//        }
//        throw new RuntimeException();
//    }
//
//
//
//
//
//    public enum ArgSortType {
//        check_context,
//        context_check,
//        check;
//    }
//}
//
//
