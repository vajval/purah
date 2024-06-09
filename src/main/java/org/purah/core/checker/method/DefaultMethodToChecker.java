package org.purah.core.checker.method;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.method.toChecker.CheckerByAnnMethod;
import org.purah.core.checker.method.toChecker.CheckerByLogicMethod;
import org.purah.core.checker.method.toChecker.MethodToChecker;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class DefaultMethodToChecker implements MethodToChecker {
    @Override
    public Checker toChecker(Object methodsToCheckersBean, Method method) {


        Parameter[] parameters = method.getParameters();
        String name = nameFromMethod(method);
        if (parameters.length == 1) {
            return new CheckerByLogicMethod(methodsToCheckersBean, method, name);
        }
        if (parameters.length == 2) {
            return new CheckerByAnnMethod(methodsToCheckersBean, method, name);
        }
        throw new RuntimeException();
    }
}
