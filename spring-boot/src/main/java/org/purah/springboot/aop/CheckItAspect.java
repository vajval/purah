package org.purah.springboot.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Pointcut("execution(* *(@org.purah.springboot.ann.CheckIt (*)))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();


        /*
         * 找到对应函数使用的检查器材，然后检查
         */
        CheckItMethodHandler.CheckOnMethod checkOnMethod = checkItMethodHandler.ofAutoReg(method);


//        boolean checkByCache = checkByCache();
//        if (checkByCache) {
//            return;
//        }
        checkOnMethod.check(joinPoint.getArgs());
//        RuleCacheThreadContext ruleCacheThreadContext = RuleCacheThreadContext.get();
//        if (ruleCacheThreadContext != null) {
//            this.checkByCache(ruleCacheThreadContext, checkOnMethod, joinPoint.getArgs());
//        } else {
//            checkOnMethod.check(joinPoint.getArgs());
//        }

    }
//
//    private boolean checkByCache(RuleCacheThreadContext ruleCacheThreadContext, CheckItAopCheckHandler.CheckOnMethod checkOnMethod, Object[] objects) {
//
//    }


//    public void checkByCache(RuleCacheThreadContext ruleCacheThreadContext, CheckItAopCheckHandler.CheckOnMethod checkOnMethod, Object[] objects) {
//        RuleCaches ruleCaches = ruleCacheThreadContext.getRuleCaches();
//
//        for (CheckItAopCheckHandler.RuleFieldConfig ruleFieldConfig : checkOnMethod.ruleFieldConfigList) {
//            Object object = objects[ruleFieldConfig.index];
//            for (Rule rule : ruleFieldConfig.ruleList) {
//                ruleCaches.check(ruleCacheThreadContext, rule, object);
//            }
//        }
//    }
//

}
