package org.purah.springboot.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.purah.core.PurahContext;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.exception.MethodArgCheckException;
import org.purah.springboot.result.AutoFillCheckResult;
import org.purah.springboot.result.MethodCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Lazy(value = false)
@Component
@Aspect
public class CheckItAspect {

    @Autowired
    PurahContext purahContext;
    private final Map<Method, MethodHandlerChecker> methodCheckerMap = new ConcurrentHashMap<>();


    @Pointcut("execution(* *(.., @org.purah.springboot.ann.CheckIt (*), ..))")
    public void pointcut() {


    }


    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        /*
         * 找到对应函数使用的检查器材，然后检查
         */
        MethodHandlerChecker methodHandlerChecker = this.checkerOf(joinPoint.getThis(), method);
        CheckInstance<Object[]> checkInstance = CheckInstance.create(joinPoint.getArgs(), Object[].class);

        MethodCheckResult methodCheckResult = methodHandlerChecker.check(checkInstance);


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
            AutoFillCheckResult autoFillCheckResult = new AutoFillCheckResult(methodCheckResult);

            return methodHandlerChecker.fillObject(autoFillCheckResult);
        }


    }

    public MethodHandlerChecker checkerOf(Object bean, Method method) {

        MethodHandlerChecker methodHandlerChecker = methodCheckerMap.computeIfAbsent(method, i -> new MethodHandlerChecker(bean, method, purahContext));

        purahContext.checkManager().reg(methodHandlerChecker);

        return methodHandlerChecker;
    }


}
