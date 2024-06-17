package org.purah.core.checker.method.converter;

import org.purah.core.checker.Checker;

import java.lang.reflect.Method;

public interface MethodToCheckerConverter {

    Checker toChecker(Object methodsToCheckersBean, Method method,String name);




}
