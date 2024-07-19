package org.purah.core.matcher;

import java.util.Set;

/**
 * field matcher for matching fields in input parameters
 */
public interface FieldMatcher {

    String rootField = "$root$";


    /**
     * ChatGPT
     * If supported, you must override equals.
     */
    default boolean supportCache() {
        return false;
    }

    /**
     * @param fields   Fields as interpreted by the Resolver.
     * @param inputArg inputArg
     * @return match Fields
     */


    Set<String> matchFields(Set<String> fields, Object inputArg);


}
