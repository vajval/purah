package org.purah.core.resolver;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

import java.util.Map;
import java.util.Set;




public interface ArgResolver {
    /**
     *
     * Retrieve the values corresponding to all fields or nested fields matched by fieldMatcher,
     * and annotation
     */

    Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputArg, FieldMatcher fieldMatcher);


    /**
     * If the input parameter is null and lacks class information,
     * it will prevent the retrieval of fields and annotations through reflection.
     */

    default Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(Object inputArg, FieldMatcher fieldMatcher) {
        return getMatchFieldObjectMap(InputToCheckerArg.of(inputArg), fieldMatcher);
    }


}
