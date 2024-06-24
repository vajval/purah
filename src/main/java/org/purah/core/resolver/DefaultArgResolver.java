package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;


import java.util.Map;
import java.util.Set;

public class DefaultArgResolver implements ArgResolver {
    ReflectArgResolver mainArgResolver = new ReflectArgResolver();


    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputArg, FieldMatcher fieldMatcher) {
        return mainArgResolver.getMatchFieldObjectMap(inputArg, fieldMatcher);
    }


    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(Object o, FieldMatcher fieldMatcher) {
        return mainArgResolver.getMatchFieldObjectMap(o, fieldMatcher);
    }


}
