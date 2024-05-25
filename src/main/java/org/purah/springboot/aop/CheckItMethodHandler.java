package org.purah.springboot.aop;


import org.purah.core.PurahContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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





}
