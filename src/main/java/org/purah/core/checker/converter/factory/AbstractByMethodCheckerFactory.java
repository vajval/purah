package org.purah.core.checker.converter.factory;

import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.exception.init.InitCheckFactoryException;
import org.purah.core.matcher.singlelevel.WildCardMatcher;

import java.lang.reflect.Method;

/**
 * wrap the method into a checker factory.
 */
public abstract class AbstractByMethodCheckerFactory implements CheckerFactory {

    protected WildCardMatcher wildCardMatcher;
    protected boolean cacheBeCreatedChecker;
    protected Method method;
    protected Object bean;
    protected String matchStr;

    public AbstractByMethodCheckerFactory(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        this.wildCardMatcher = new WildCardMatcher(matchStr);
        this.cacheBeCreatedChecker = cacheBeCreatedChecker;
        this.method = method;
        this.bean = bean;
        this.matchStr = matchStr;
        String errorMsg = errorMsgBaseCheckerFactoryByMethod(bean, method);
        if (errorMsg != null) {
            throw new InitCheckFactoryException(errorMsg);
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
            return "Hmph, the method must not be null!";
        }


        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            return "if the method isn't public, it just won't work, okay? [" + method.toGenericString() + "]";
        }

        boolean isStatic = java.lang.reflect.Modifier.isStatic(method.getModifiers());

        if (!isStatic && bean == null) {
            return "When the method is non-static, the parameter `bean` must not be null. [" + method.toGenericString() + "]";
        }
        if (bean != null) {
            boolean clazzIsPublic = java.lang.reflect.Modifier.isPublic(bean.getClass().getModifiers());
            if (!clazzIsPublic) {
                return "if the bean class isn't public, it just won't work, okay? [" + method.toGenericString() + "]";
            }
        }

        return null;


    }
}
