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
    protected final Map<Method, CheckOnMethod> methodCheckerMap = new ConcurrentHashMap<>();


    public CheckItMethodHandler(PurahContext purahContext) {
        this.purahContext = purahContext;
    }

    public void refresh() {
        methodCheckerMap.clear();
    }

    public CheckOnMethod ofAutoReg(Method method) {
        return methodCheckerMap.computeIfAbsent(method, i -> buildCheckOnMethod(method));
    }

    /**
     * 输入带有 checkIt 注解的 函数
     * 注册需要对每个参数使用的检查器
     */

    private CheckOnMethod buildCheckOnMethod(Method method) {

        ArrayList<Parameter> parameterArrayList = Lists.newArrayList(method.getParameters());
        List<MethodArgCheckConfig> methodArgCheckConfigs = new ArrayList<>();

        int index = -1;
        for (Parameter parameter : parameterArrayList) {
            CheckIt checkIt = parameter.getAnnotation(CheckIt.class);
            index++;
            if (checkIt == null) continue;
            /*
             * 找到有注解的参数
             */

            List<String> useCheckerNameList = Stream.of(checkIt.value()).toList();
            Class<?> argClazz = parameter.getType();
            if (useCheckerNameList.size() == 0) {

                CheckIt argClazzCheckIt = argClazz.getDeclaredAnnotation(CheckIt.class);
                if (argClazzCheckIt == null) continue;
                useCheckerNameList = Stream.of(argClazzCheckIt.value()).toList();

            }
            if (useCheckerNameList.size() == 0) continue;


            MethodArgCheckConfig methodArgCheckConfig = new MethodArgCheckConfig();

            methodArgCheckConfig.setCheckItAnn(checkIt);
            methodArgCheckConfig.setClazz(argClazz);
            methodArgCheckConfig.setIndex(index);

            List<Checker> checkerList = useCheckerNameList.stream().map(i -> purahContext.checkManager().get(i)).collect(Collectors.toList());
            methodArgCheckConfig.setCheckerList(checkerList);

            methodArgCheckConfigs.add(methodArgCheckConfig);
        }

        return new CheckOnMethod(method, methodArgCheckConfigs);
    }


    /**
     * 在指定的函数被调用时，执行此类中的方法对入参进行校验
     * 一个函数中可能对多个入参使用CheckIt 注解进行了检查
     * 所以用list来保存配置
     */
    public static class CheckOnMethod {
        Method method;


        boolean fillToMethodResult;


        Class<?> returnType;

        List<MethodArgCheckConfig> methodArgCheckConfigList;

        protected CheckOnMethod(Method method, List<MethodArgCheckConfig> methodArgCheckConfigList) {
            this.method = method;
            this.methodArgCheckConfigList = methodArgCheckConfigList;
            this.returnType = this.method.getReturnType();
            fillToMethodResult = (method.getDeclaredAnnotation(FillToMethodResult.class)) != null;
            if (fillToMethodResult) {
                if (!(CheckerResult.class.isAssignableFrom(returnType)) &&
                        !(Boolean.class.isAssignableFrom(returnType)) &&
                        !(boolean.class.isAssignableFrom(returnType))) {
                    throw new RuntimeException("返回值必须是 CheckerResult Boolean 或者 boolean " + method);

                }
            }
        }

        public CombinatorialCheckerResult check(Object... args) {
            CombinatorialCheckerResult result = new CombinatorialCheckerResult();
            for (MethodArgCheckConfig methodArgCheckConfig : methodArgCheckConfigList) {
                List<CheckerResult> childRusultList = this.check(methodArgCheckConfig, args[methodArgCheckConfig.index]);
                for (CheckerResult childResult : childRusultList) {
                    result.addResult(childResult);
                }
                if (result.isFailed()) {
                    return result;
                }
            }
            return result;
        }


        private List<CheckerResult> check(MethodArgCheckConfig methodArgCheckConfig, Object arg) {
            List<CheckerResult> resultList = new ArrayList<>();
            List<Checker> checkerList = methodArgCheckConfig.checkerList;
            for (Checker checker : checkerList) {
                CheckerResult ruleResult = checker.check(CheckInstance.create(arg));
                resultList.add(ruleResult);
                if (ruleResult.isFailed()) return resultList;

            }
            return resultList;

        }
    }


    static class MethodArgCheckConfig {

        /**
         * 注解内容
         */
        CheckIt checkItAnn;
        /**
         * 校验用的规则
         */

        List<Checker> checkerList;
        /**
         * 入参类型
         */
        Class<?> clazz;
        /**
         * 入参的位置
         */

        int index;

        public CheckIt getCheckItAnn() {
            return checkItAnn;
        }

        public void setCheckItAnn(CheckIt checkItAnn) {
            this.checkItAnn = checkItAnn;
        }

        public List<Checker> getCheckerList() {
            return checkerList;
        }

        public void setCheckerList(List<Checker> checkerList) {
            this.checkerList = checkerList;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


}
