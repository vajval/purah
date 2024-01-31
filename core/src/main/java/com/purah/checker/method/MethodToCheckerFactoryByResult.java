package com.purah.checker.method;

import com.purah.base.PurahEnableMethod;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.Method;
import java.util.function.Function;

public class MethodToCheckerFactoryByResult implements CheckerFactory {


    WildCardMatcher wildCardMatcher;

    PurahEnableMethod purahEnableMethod;

    public MethodToCheckerFactoryByResult(Method method, Object bean, String matchStr) {
        PurahEnableMethod purahEnableMethod = new PurahEnableMethod(bean,method);
        this.wildCardMatcher = new WildCardMatcher(matchStr);

    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return wildCardMatcher.match(needMatchCheckerName);
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        return null;
    }
    //    @Override
//    public Checker createChecker(String needMatchCheckerName) {
//
//        return new BaseChecker() {
//            @Override
//            public CheckerResult doCheck(CheckInstance checkInstance) {
//
//                return method.invoke(bean, checkInstance.instance());
//
//            }
//        };
//    }

}
