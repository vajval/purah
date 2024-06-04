package org.purah.core.checker.factory.bymethod;

import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerProxy;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CheckerFactoryByMethod implements CheckerFactory {


    WildCardMatcher wildCardMatcher;


    Method method;
    Object bean;
    boolean cacheBeCreatedChecker;

    public CheckerFactoryByMethod(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        this.wildCardMatcher = new WildCardMatcher(matchStr);
        this.method = method;
        this.bean = bean;
        this.cacheBeCreatedChecker = cacheBeCreatedChecker;
    }

    @Override
    public boolean cacheBeCreatedChecker() {
        return this.cacheBeCreatedChecker;
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
            if (StringUtils.hasText(result.name())) {
                name = result.name();
            }
            return new CheckerProxy(result, name, method.toGenericString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
