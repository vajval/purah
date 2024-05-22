package org.purah.springboot.aop;



import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.result.CombinatorialCheckerResult;
import org.purah.core.exception.ArgCheckException;
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

    static int w = 0;

    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();


        /*
         * 找到对应函数使用的检查器材，然后检查
         */

        MethodHandlerChecker methodHandlerChecker = checkItMethodHandler.checkerOf(joinPoint.getThis(), method);

        CombinatorialCheckerResult checkerResult = (CombinatorialCheckerResult)methodHandlerChecker.check(CheckInstance.create(joinPoint.getArgs()));


        if (checkerResult.isError()) {
            checkerResult.exception().printStackTrace();
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
