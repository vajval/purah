package org.purah.core.checker.method.toChecker;

import org.purah.core.checker.base.Checker;

import java.lang.reflect.Method;

public interface MethodToChecker {

    Checker toChecker(Object methodsToCheckersBean, Method method,String name);




}
