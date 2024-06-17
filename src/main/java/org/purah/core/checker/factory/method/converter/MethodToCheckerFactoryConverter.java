package org.purah.core.checker.factory.method.converter;

import org.purah.core.checker.factory.CheckerFactory;

import java.lang.reflect.Method;

public interface MethodToCheckerFactoryConverter {

    CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker);

}
