package io.github.vajval.purah.core.checker.converter.checker;


import io.github.vajval.purah.core.checker.AbstractBaseSupportCacheChecker;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.ExecInfo;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.PurahWrapMethod;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 函数转checker
 * wrap the method into a checker.
 */
public abstract class AbstractWrapMethodToChecker extends AbstractBaseSupportCacheChecker<Object, Object> {


    protected PurahWrapMethod purahEnableMethod;

    protected String name;

    protected Method method;

    protected AutoNull autoNull;

    protected String failedInfo;
    boolean failedInfoReplace;

    public AbstractWrapMethodToChecker(Object methodsToCheckersBean, Method method, String name, AutoNull autoNull, String failedInfo) {
        String errorMsg = errorMsgAbstractMethodToChecker(methodsToCheckersBean, method, name);
        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }
        this.name = name;
        this.method = method;
        this.autoNull = autoNull;
        this.failedInfo = failedInfo;
        this.failedInfoReplace = this.failedInfo.contains("$");
    }


    public static String errorMsgAbstractMethodToChecker(Object methodsToCheckersBean, Method method, String name) {
        if (!StringUtils.hasText(name)) {
            return "Hmph, name  must has text!";
        }
        if (method == null) {
            return "Hmph, the method must not be null!";
        }
        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            return "if the method isn't public, it just won't work, okay? [" + method.toGenericString() + "]";
        }
        boolean isStatic = java.lang.reflect.Modifier.isStatic(method.getModifiers());
        if (!isStatic && methodsToCheckersBean == null) {
            return "When the method is non-static, the parameter `bean` must not be null. [" + method.toGenericString() + "]";
        }
        if (methodsToCheckersBean != null) {
            boolean clazzIsPublic = java.lang.reflect.Modifier.isPublic(methodsToCheckersBean.getClass().getModifiers());
            if (!clazzIsPublic) {
                return "if the bean class isn't public, it just won't work, okay? [" + method.toGenericString() + "]";
            }
        }
        return null;
    }


    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        if (inputToCheckerArg.isNull() && autoNull != AutoNull.notEnable) {
            if (autoNull == AutoNull.ignore) {
                return LogicCheckResult.ignore();
            }
            if (autoNull == AutoNull.failed) {
                return LogicCheckResult.failed(null, "auto null failed " + purahEnableMethod.log(inputToCheckerArg)).updateInfo(LogicCheckResult.autoInfo(inputToCheckerArg, failedInfo));

            }
            if (autoNull == AutoNull.success) {
                return LogicCheckResult.success();
            }
        }
        CheckResult<Object> result = methodDoCheck(inputToCheckerArg);
        if (result.execInfo() == ExecInfo.failed) {
            return result.updateInfo(LogicCheckResult.autoInfo(inputToCheckerArg, failedInfo));
        }
        return result;
    }

    public abstract CheckResult<Object> methodDoCheck(InputToCheckerArg<Object> inputToCheckerArg);

    @Override
    public String name() {
        return name;
    }


    @Override
    public Class<?> inputArgClass() {
        return purahEnableMethod.needCheckArgClass();
    }

    @Override
    public Class<?> resultDataClass() {
        return purahEnableMethod.resultDataClass();
    }


    @Override
    public String logicFrom() {
        return purahEnableMethod.logicFrom();
    }




}