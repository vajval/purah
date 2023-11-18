//package com.purah.checker;
//
//import com.vajva.rule.RuleContext;
//
//public class MethodChecker<CHECK_INSTANCE,T> implements Checker<CHECK_INSTANCE,T> {
//
//    CheckerMethod checkerMethod;
//
//    public MethodChecker(CheckerMethod checkerMethod) {
//        this.checkerMethod = checkerMethod;
//
//    }
//
//
//    @Override
//    public CheckerResult<T> check(CheckInstance checkInstance, CHECK_INSTANCE check_instance) {
//
//        return (CheckerResult)checkerMethod.invoke(checkInstance, check_instance);
//    }
//
//    @Override
//    public String name() {
//        return checkerMethod.name;
//    }
//}
