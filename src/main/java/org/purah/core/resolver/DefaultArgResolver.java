package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.matcher.intf.FieldMatcher;


import java.util.Map;
import java.util.Set;

public class DefaultArgResolver implements ArgResolver{
    ReflectArgResolver mainArgResolver = new ReflectArgResolver();






    @Override
    public boolean support(Class<?> clazz) {
        return mainArgResolver.support(clazz);
    }

    @Override
    public Map<String, CheckInstance<?>> getMatchFieldObjectMap(Object o, FieldMatcher fieldMatcher) {
        return mainArgResolver.getMatchFieldObjectMap(o, fieldMatcher);
    }


    @Override
    public Set<Class<?>> supportTypes() {
        return Sets.newHashSet(Object.class);
    }
}
