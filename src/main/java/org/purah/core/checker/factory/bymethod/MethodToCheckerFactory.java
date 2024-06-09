package org.purah.core.checker.factory.bymethod;

import org.purah.core.base.NameUtil;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public interface MethodToCheckerFactory {

    CheckerFactory toCheckerFactory(Object bean, Method method, boolean cacheBeCreatedChecker);


    default String errorMsg(Method method) {
        if (method == null) {
            return "不支持null method";
        }
        if (method.getModifiers() != java.lang.reflect.Modifier.PUBLIC) {
            return "非public 不生效" + method.toGenericString();
        }
        String name = NameUtil.nameByAnnOnMethod(method);
        if (!StringUtils.hasText(name)) {
            return "请在类上加名字注解";
        }
        return null;

    }
}
