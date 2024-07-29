package io.github.vajval.purah.spring.aop;


import io.github.vajval.purah.spring.aop.ann.CheckIt;
import io.github.vajval.purah.spring.aop.exception.MethodArgCheckException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.cache.PurahCheckInstanceCacheContext;
import io.github.vajval.purah.spring.aop.ann.MethodCheckConfig;
import io.github.vajval.purah.spring.aop.result.MethodHandlerCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Lazy(value = false)
@Aspect
public class CheckItAspect {


    private static final Logger log = LogManager.getLogger(CheckItAspect.class);

    @Autowired
    Purahs purahs;
    @Autowired
    PurahContext purahContext;
    private final Map<Method, MethodHandlerChecker> methodCheckerMap = new ConcurrentHashMap<>();



    /*
     * 切面点
     */

    @Pointcut("execution(* *(.., @io.github.vajval.purah.spring.aop.ann.CheckIt (*), ..))")
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
        MethodCheckConfig methodCheckConfig = method.getDeclaredAnnotation(MethodCheckConfig.class);
        if (methodCheckConfig == null) {
            return purahContext.config().isCache();
        }
        return methodCheckConfig.enableCache();
    }

    public Object pointcut(ProceedingJoinPoint joinPoint) {


        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        MethodHandlerChecker methodHandlerChecker   =  methodCheckerMap.computeIfAbsent(method, i -> new MethodHandlerChecker(joinPoint.getThis(), method, purahs));

        InputToCheckerArg<Object[]> inputToCheckerArg = InputToCheckerArg.of(joinPoint.getArgs(), Object[].class);

        MethodHandlerCheckResult methodHandlerCheckResult = methodHandlerChecker.check(inputToCheckerArg);


        if (methodHandlerCheckResult.isError()) {
            log.error(methodHandlerCheckResult.exception());
            throw new MethodArgCheckException(methodHandlerCheckResult);
        }
        boolean fillToMethodResult = methodHandlerChecker.isFillToMethodResult();
        if (methodHandlerCheckResult.isFailed()) {
            if (!fillToMethodResult) {
                throw new MethodArgCheckException(methodHandlerCheckResult);
            }
        }

        Object invokeObject;
        try {
            invokeObject = joinPoint.proceed();
        }  catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (fillToMethodResult) {
            return methodHandlerChecker.fillObject(methodHandlerCheckResult);
        } else {
            return invokeObject;
        }


    }



}
