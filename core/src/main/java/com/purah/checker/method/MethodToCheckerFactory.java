package com.purah.checker.method;

import com.purah.checker.Checker;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.Method;

public class MethodToCheckerFactory implements CheckerFactory {


    WildCardMatcher wildCardMatcher;

    Method method;

    public MethodToCheckerFactory(Method method, String matchStr) {

        this.wildCardMatcher = new WildCardMatcher(matchStr);
        this.method = method;
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return wildCardMatcher.match(needMatchCheckerName);
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        return null;
    }
}
