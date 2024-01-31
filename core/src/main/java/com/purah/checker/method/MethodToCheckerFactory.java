package com.purah.checker.method;

import com.google.common.collect.Lists;
import com.purah.base.PurahEnableMethod;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodToCheckerFactory implements CheckerFactory {


    WildCardMatcher wildCardMatcher;


    PurahEnableMethod purahEnableMethod;
    Method method;
    Object bean;


    public MethodToCheckerFactory(Object bean, Method method, String matchStr) {
        this.wildCardMatcher = new WildCardMatcher(matchStr);
        this.method = method;
        this.bean = bean;
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return wildCardMatcher.match(needMatchCheckerName);
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        try {
            return (Checker) method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
