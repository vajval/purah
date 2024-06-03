package org.purah.core.checker.factory.bymethod;

import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.Method;

public interface MethodToCheckerFactory {

    CheckerFactory toCheckerFactory(Object bean, Method method);
}
