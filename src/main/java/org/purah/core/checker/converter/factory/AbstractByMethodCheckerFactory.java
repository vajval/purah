package org.purah.core.checker.converter.factory;

import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.WildCardMatcher;

import java.lang.reflect.Method;

public abstract class AbstractByMethodCheckerFactory implements CheckerFactory {

    protected WildCardMatcher wildCardMatcher;
    protected boolean cacheBeCreatedChecker;
    protected   Method method;
    protected   Object bean;
    protected   String matchStr;

    public AbstractByMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        this.wildCardMatcher = new WildCardMatcher(matchStr);
        this.cacheBeCreatedChecker = cacheBeCreatedChecker;
        this.method = method;
        this.bean = bean;
        this.matchStr = matchStr;
        String errorMsg = errorMsgBaseCheckerFactoryByMethod(bean, method);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }
    }

    @Override
    public String name() {
        return this.matchStr;
    }

    @Override
    public boolean cacheBeCreatedChecker() {
        return this.cacheBeCreatedChecker;
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return wildCardMatcher.match(needMatchCheckerName);
    }

    public static String errorMsgBaseCheckerFactoryByMethod(Object bean, Method method) {

        if (method == null) {
            return "不支持null method";
        }
        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            return "非public 不生效" + method.toGenericString();
        }
        boolean isStatic = java.lang.reflect.Modifier.isStatic(method.getModifiers());
        if (!isStatic && bean == null) {
            return "非静态函数 bean 不能为null" + method.toGenericString();
        }

        return null;


    }
}
