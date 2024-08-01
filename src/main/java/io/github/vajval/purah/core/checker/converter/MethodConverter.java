package io.github.vajval.purah.core.checker.converter;

import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.converter.checker.AutoNull;
import io.github.vajval.purah.core.checker.factory.CheckerFactory;

import java.lang.reflect.Method;

public interface MethodConverter {



    Checker<?,?> toChecker(Object methodsToCheckersBean, Method method, String name, AutoNull autoNull);


    CheckerFactory toCheckerFactory(Object bean, Method method, String match, boolean cacheBeCreatedChecker);





}
