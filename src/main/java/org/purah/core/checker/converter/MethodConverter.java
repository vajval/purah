package org.purah.core.checker.converter;

import org.purah.core.checker.Checker;
import org.purah.core.checker.factory.CheckerFactory;

import java.lang.reflect.Method;

public interface MethodConverter {



    Checker<?,?> toChecker(Object methodsToCheckersBean, Method method,String name);


    CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker);





}
