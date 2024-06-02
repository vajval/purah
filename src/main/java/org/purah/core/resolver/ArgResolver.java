package org.purah.core.resolver;



import org.purah.core.checker.base.CheckInstance;
import org.purah.core.matcher.intf.FieldMatcher;

import java.util.Map;
import java.util.Set;


public interface ArgResolver{

    Map<String, CheckInstance<?>> getMatchFieldObjectMap(Object inputArg, FieldMatcher fieldMatcher);

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
