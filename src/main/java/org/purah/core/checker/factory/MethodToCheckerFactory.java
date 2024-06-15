package org.purah.core.checker.factory;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public interface MethodToCheckerFactory {

    CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker);

}
