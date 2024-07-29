package io.github.vajval.purah.core.resolver;

import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.checker.InputToCheckerArg;


import java.util.Map;

public class DefaultArgResolver implements ArgResolver {
    final ReflectArgResolver mainArgResolver = new ReflectArgResolver();
    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputArg, FieldMatcher fieldMatcher) {
        return mainArgResolver.getMatchFieldObjectMap(inputArg, fieldMatcher);
    }


    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(Object o, FieldMatcher fieldMatcher) {
        return mainArgResolver.getMatchFieldObjectMap(o, fieldMatcher);
    }


}
