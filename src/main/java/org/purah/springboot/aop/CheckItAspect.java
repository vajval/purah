package org.purah.springboot.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.purah.core.checker.CheckInstance;
import org.purah.core.exception.MethodArgCheckException;
import org.purah.springboot.result.AutoFillCheckResult;
import org.purah.springboot.result.MethodCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Lazy(value = false)
@Component
@Aspect
public class CheckItAspect {

    @Autowired
    CheckItMethodHandler checkItMethodHandler;
    @Autowired
    ApplicationContext applicationContext;

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

        MethodHandlerChecker methodHandlerChecker = checkItMethodHandler.checkerOf(joinPoint.getThis(), method);

        MethodCheckResult methodCheckResult = (MethodCheckResult) methodHandlerChecker.check(CheckInstance.create(joinPoint.getArgs()));


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


}
