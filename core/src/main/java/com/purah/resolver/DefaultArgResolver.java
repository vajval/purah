package com.purah.resolver;

import com.google.common.collect.Sets;
import com.purah.checker.CheckInstance;
import com.purah.matcher.intf.FieldMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultArgResolver implements ArgResolver<Object> {
    Map<Class<?>, ArgResolver> argResolverMap = new HashMap<>();

    public DefaultArgResolver() {
        argResolverMap.put(Object.class, new ReflectArgResolver());
        argResolverMap.put(Map.class, new MapStringObjectArgResolver());

    }


    @Override
    public Map<String, CheckInstance> getMatchFieldObjectMap(Object o, FieldMatcher fieldMatcher) {
        if(o==null){
            throw new RuntimeException();
        }
        ArgResolver argResolver;
        if (Map.class.isAssignableFrom(o.getClass())) {
            argResolver = argResolverMap.get(Map.class);
        } else {
            argResolver = argResolverMap.get(Object.class);
        }
        return argResolver.getMatchFieldObjectMap(o, fieldMatcher);
    }

    @Override
    public Set<Class<?>> supportTypes() {
        return Sets.newHashSet(Object.class);
    }
}
