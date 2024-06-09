package org.purah.core.checker.method.toChecker;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.Checker;
import org.purah.springboot.ann.ToChecker;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public interface MethodToChecker {

    Checker toChecker(Object methodsToCheckersBean, Method method);

    default String errorMsg(Method method) {
        if (method == null) {
            return "不支持null method";
        }
        if (method.getModifiers() != java.lang.reflect.Modifier.PUBLIC) {
            return "非public 不生效" + method.toGenericString();
        }
        String name = nameFromMethod(method);
        if (!StringUtils.hasText(name)) {
            return "请加上name或带有name值的tochecker注解 " + method.toGenericString();
        }

        return null;

    }

    default String nameFromMethod(Method method) {
        ToChecker toChecker = method.getDeclaredAnnotation(ToChecker.class);
        String name = null;
        if (toChecker != null) {
            name = toChecker.name();
        }
        if (StringUtils.hasText(name)) {
            return name;
        }
        name = NameUtil.nameByAnnOnMethod(method);
        if (StringUtils.hasText(name)) {
            return name;
        }
        return null;
    }
}
