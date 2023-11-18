package com.purah.resolver;


import com.purah.matcher.intf.FieldMatcher;

import java.util.Map;
import java.util.Set;

/**
 * 规则解析器会根据 <INSTANCE> 来为 对象挑选合适的解析器
 */
public interface ArgResolver<INSTANCE> {

    Map<String,Object> getMatchFieldObjectMap(INSTANCE instance, FieldMatcher fieldMatcher);

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
