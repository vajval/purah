package com.purah.springboot.aop;


import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.exception.ArgCheckException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Method;

@Lazy(value = false)
@Component
@Aspect
public class CheckItAspect {

    @Autowired
    CheckItMethodHandler checkItMethodHandler;
    @Autowired
    ApplicationContext applicationContext;

    @Pointcut("execution(* *(.., @com.purah.springboot.ann.CheckIt (*), ..))")
    public void pointcut() {


    }

    static int w = 0;

    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();


        /*
         * 找到对应函数使用的检查器材，然后检查
         */

        MethodHandlerChecker methodHandlerChecker = checkItMethodHandler.checkerOf(joinPoint.getThis(), method);

        CombinatorialCheckerResult checkerResult = methodHandlerChecker.check(CheckInstance.create(joinPoint.getArgs()));


        if (checkerResult.isError()) {
            throw new ArgCheckException(checkerResult);
        }


        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (!methodHandlerChecker.isFillToMethodResult()) {
            if (checkerResult.isFailed()) {
                throw new ArgCheckException(checkerResult);
            }
            return result;
        }
        boolean resultIsCheckResultClass = methodHandlerChecker.resultIsCheckResultClass();

        if (resultIsCheckResultClass) {
            return checkerResult;
        }
        return checkerResult.isSuccess();

    }


}
