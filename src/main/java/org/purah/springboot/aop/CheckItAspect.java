package org.purah.springboot.aop;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.purah.core.PurahContext;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import org.purah.core.exception.MethodArgCheckException;
import org.purah.springboot.aop.ann.MethodCheck;
import org.purah.springboot.aop.result.MethodCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Lazy(value = false)
@Component
@Aspect
public class CheckItAspect {


    private static final Logger log = LogManager.getLogger(CheckItAspect.class);

    @Autowired
    PurahContext purahContext;
    private final Map<Method, MethodHandlerChecker> methodCheckerMap = new ConcurrentHashMap<>();


    @Pointcut("execution(* *(.., @org.purah.springboot.aop.ann.CheckIt (*), ..))")
    public void pointcut() {


    }


    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        boolean enableCache = enableCache(method);
        if (enableCache) {
            return PurahCheckInstanceCacheContext.execOnCacheContext(() -> pointcut(joinPoint));
        }
        return pointcut(joinPoint);


    }


    public boolean enableCache(Method method) {
        MethodCheck methodCheck = method.getDeclaredAnnotation(MethodCheck.class);
        if (methodCheck == null) {
            return purahContext.config().isCache();
        }
        return methodCheck.enableCache();
    }

    public Object pointcut(ProceedingJoinPoint joinPoint) {


        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();


        MethodHandlerChecker methodHandlerChecker = this.checkerOf(joinPoint.getThis(), method);
        InputToCheckerArg<Object[]> inputToCheckerArg = InputToCheckerArg.of(joinPoint.getArgs(), Object[].class);

        MethodCheckResult methodCheckResult = methodHandlerChecker.check(inputToCheckerArg);


        if (methodCheckResult.isError()) {
            methodCheckResult.exception().printStackTrace();
            throw new MethodArgCheckException(methodCheckResult);
        }


        Object invokeObject;
        try {
            invokeObject = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (!methodHandlerChecker.isFillToMethodResult()) {
            if (methodCheckResult.isFailed()) {
                throw new MethodArgCheckException(methodCheckResult);
            }
            return invokeObject;
        } else {

            return methodHandlerChecker.fillObject(methodCheckResult);
        }


    }

    public MethodHandlerChecker checkerOf(Object bean, Method method) {

        MethodHandlerChecker methodHandlerChecker = methodCheckerMap.computeIfAbsent(method, i -> new MethodHandlerChecker(bean, method, purahContext));
        purahContext.checkManager().reg(methodHandlerChecker);
        return methodHandlerChecker;
    }


}
