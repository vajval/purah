package org.purah.core.resolver;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

import java.util.Map;
import java.util.Set;


public interface ArgResolver {

    Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputArg, FieldMatcher fieldMatcher);

    default Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(Object inputArg, FieldMatcher fieldMatcher) {
        return getMatchFieldObjectMap(InputToCheckerArg.of(inputArg), fieldMatcher);
    }

}
