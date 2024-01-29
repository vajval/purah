package com.purah.springboot.aop;

import com.google.common.collect.Lists;
import com.purah.PurahContext;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.springboot.ann.FillToMethodResult;
import com.purah.springboot.ann.CheckIt;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 对带有CheckIt的函数入参 进行校验检查
 */

public class CheckItMethodHandler {
    PurahContext purahContext;
    /**
     * 在指定的函数执行时对 入参进行校验检查
     */
    protected final Map<Method, MethodHandlerChecker> methodCheckerMap = new ConcurrentHashMap<>();


    public CheckItMethodHandler(PurahContext purahContext) {
        this.purahContext = purahContext;
    }

    public void refresh() {
        methodCheckerMap.clear();
    }

    public MethodHandlerChecker checkerOf(Object bean, Method method) {

        return methodCheckerMap.computeIfAbsent(method, i -> new MethodHandlerChecker(bean, method, purahContext));
    }



    /**
     * 在指定的函数被调用时，执行此类中的方法对入参进行校验
     * 一个函数中可能对多个入参使用CheckIt 注解进行了检查
     * 所以用list来保存配置
     */
//    public static class CheckOnMethod {
//        Method method;
//
//
//        boolean fillToMethodResult;
//
//
//        Class<?> returnType;
//
//        List<MethodArgCheckConfig> methodArgCheckConfigList;
//
//        protected CheckOnMethod(Method method, List<MethodArgCheckConfig> methodArgCheckConfigList) {
//            this.method = method;
//            this.methodArgCheckConfigList = methodArgCheckConfigList;
//            this.returnType = this.method.getReturnType();
//            fillToMethodResult = (method.getDeclaredAnnotation(FillToMethodResult.class)) != null;
//            if (fillToMethodResult) {
//                if (!(CheckerResult.class.isAssignableFrom(returnType)) &&
//
//                        !(boolean.class.isAssignableFrom(returnType))) {
//                    throw new RuntimeException("返回值必须是 CheckerResult  或者 boolean " + method);
//
//                }
//            }
//        }
//
//        public CombinatorialCheckerResult check(Object... args) {
//            CombinatorialCheckerResult result = new CombinatorialCheckerResult();
//            for (MethodArgCheckConfig methodArgCheckConfig : methodArgCheckConfigList) {
//                List<CheckerResult> childRusultList = this.check(methodArgCheckConfig, args[methodArgCheckConfig.index]);
//                for (CheckerResult childResult : childRusultList) {
//                    result.addResult(childResult);
//                }
//                if (result.isFailed()) {
//                    return result;
//                }
//            }
//            return result;
//        }
//
//
//        private List<CheckerResult> check(MethodArgCheckConfig methodArgCheckConfig, Object arg) {
//            List<CheckerResult> resultList = new ArrayList<>();
//            List<Checker> checkerList = methodArgCheckConfig.checkerList;
//            for (Checker checker : checkerList) {
//                CheckerResult ruleResult = checker.check(CheckInstance.create(arg));
//                resultList.add(ruleResult);
//                if (ruleResult.isFailed()) return resultList;
//
//            }
//            return resultList;
//
//        }
//    }


}
