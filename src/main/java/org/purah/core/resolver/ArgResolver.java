package org.purah.core.resolver;


import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

import java.util.Map;
import java.util.Set;


public interface ArgResolver {

    Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputArg, FieldMatcher fieldMatcher);

    default Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(Object inputArg, FieldMatcher fieldMatcher) {
        return getMatchFieldObjectMap(InputToCheckerArg.create(inputArg), fieldMatcher);
    }

    Set<Class<?>> supportTypes();


    default boolean support(Class<?> clazz) {
        if (supportTypes().contains(clazz)) {
            return true;
        }
        for (Class<?> supportType : supportTypes()) {
            //  Map                        HashMap
            if (supportType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }


}
