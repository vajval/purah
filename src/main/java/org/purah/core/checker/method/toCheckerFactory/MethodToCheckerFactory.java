package org.purah.core.checker.method.toCheckerFactory;

import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerProxy;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.method.toChecker.PurahEnableMethod;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

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
