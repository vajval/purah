package org.purah.core.checker.method;

import com.google.common.collect.Lists;
import org.purah.core.base.Name;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.result.CheckResult;

import java.lang.reflect.Method;


public class BaseLogicMethodToChecker extends MethodToChecker {

    public static PurahEnableMethodValidator methodToCheckerValidator = new PurahEnableMethodValidator(Lists.newArrayList(Name.class),
            Lists.newArrayList(Object.class), Lists.newArrayList(boolean.class, CheckResult.class)
    );

    public BaseLogicMethodToChecker(Object methodsToCheckersBean, Method method) {
        super(methodsToCheckersBean, method);
        this.name = method.getAnnotation(Name.class).value();

    }

    @Override
    public PurahEnableMethod purahEnableMethod(Object methodsToCheckersBean, Method method) {
        return new PurahEnableMethod(methodsToCheckersBean, method);
    }
    public static String errorMsg(Object methodsToCheckersBean, Method method) {
        return methodToCheckerValidator.errorMsg(methodsToCheckersBean, method);
    }
    public static boolean enable(Object methodsToCheckersBean, Method method) {
        return methodToCheckerValidator.enable(methodsToCheckersBean, method);
    }

    @Override
    public CheckResult doCheck(CheckInstance checkInstance) {
        Object[] args = new Object[1];
        args[0] = purahEnableMethod.checkInstanceToInputArg(checkInstance);


        return purahEnableMethod.invoke(args);
    }

    @Override
    protected PurahEnableMethodValidator validator() {
        return methodToCheckerValidator;
    }

}

