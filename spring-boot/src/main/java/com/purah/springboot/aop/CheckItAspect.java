package com.purah.springboot.aop;


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

import java.lang.reflect.Method;

@Lazy(value = false)
@Component
@Aspect
public class CheckItAspect {

    @Autowired
    CheckItMethodHandler checkItMethodHandler;
    @Autowired
    ApplicationContext applicationContext;


    @Pointcut("execution(* *(@com.purah.springboot.ann.CheckIt (*)))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();


        /*
         * 找到对应函数使用的检查器材，然后检查
         */
        CheckItMethodHandler.CheckOnMethod checkOnMethod = checkItMethodHandler.ofAutoReg(method);


        CombinatorialCheckerResult checkerResult = checkOnMethod.check(joinPoint.getArgs());


        if (checkerResult.isError()) {
            throw new ArgCheckException(checkerResult);
        }


        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (!checkOnMethod.fillToMethodResult) {
            if (checkerResult.isFailed()) {
                throw new ArgCheckException(checkerResult);
            }
            return result;
        }

        if (checkOnMethod.returnType == Boolean.class || checkOnMethod.returnType == boolean.class) {
            return checkerResult.isSuccess();
        } else if (CheckerResult.class.isAssignableFrom(checkOnMethod.returnType)) {
            return checkerResult;
        }
        throw new RuntimeException("不應該有錯誤");
    }


}
