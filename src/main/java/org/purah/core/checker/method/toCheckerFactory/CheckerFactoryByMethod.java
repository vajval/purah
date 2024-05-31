package org.purah.core.checker.method.toCheckerFactory;

import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerProxy;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CheckerFactoryByMethod implements CheckerFactory {


    WildCardMatcher wildCardMatcher;


    Method method;
    Object bean;


    public CheckerFactoryByMethod(Object bean, Method method, String matchStr) {
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
            Checker result = (Checker) method.invoke(bean, needMatchCheckerName);
            String name = needMatchCheckerName;
            if (result.name() != null) {
                name = result.name();
            }
            return new CheckerProxy(result, name, method.toGenericString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
