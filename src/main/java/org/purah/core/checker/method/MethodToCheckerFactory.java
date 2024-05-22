package org.purah.core.checker.method;

import org.purah.core.base.PurahEnableMethod;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerProxy;
import org.purah.core.checker.factory.CheckerFactory;
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

        Method fMethod = method;

        try {
            Checker result = (Checker) method.invoke(bean, needMatchCheckerName);
            return new CheckerProxy(result) {
                @Override
                public String logicFrom() {
                    return fMethod.toGenericString();
                }
            };
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
