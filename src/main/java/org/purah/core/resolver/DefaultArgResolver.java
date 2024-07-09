package org.purah.core.resolver;

import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;


import java.util.Map;

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
