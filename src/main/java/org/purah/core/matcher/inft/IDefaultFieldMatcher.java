package org.purah.core.matcher.inft;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation
 */
public interface IDefaultFieldMatcher extends FieldMatcher {

    /**
     * Other functions are for debugging convenience.
     * Only this function will be called by the resolver in Purah.
     * It is not placed in FieldMatcher to avoid confusion.
     */

    default Set<String> matchFields(Set<String> fields, Object belongInstance) {
        HashSet<String> result = new HashSet<>();
        for (String field : fields) {
            if (this.match(field, belongInstance)) {
                result.add(field);
            }
        }
        return result;
    }

    boolean match(String field, Object belongInstance);

    default boolean match(String field) {
        return match(field, null);
    }


    default Set<String> matchFields(Set<String> fields) {
        HashSet<String> result  = new HashSet<>();
        for (String field : fields) {
            if (this.match(field)) {
                result.add(field);
            }
        }
        return result;

    }


}
