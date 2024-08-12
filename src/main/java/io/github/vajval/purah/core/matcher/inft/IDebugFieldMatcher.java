package io.github.vajval.purah.core.matcher.inft;

import io.github.vajval.purah.core.matcher.FieldMatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * 默认实现,只是方便调试
 * 入口只有  Set<String> matchFields(Set<String> fields, Object belongInstance)
 * Default implementation
 */
public interface IDebugFieldMatcher extends FieldMatcher {

    /*
     * Other functions are for debugging convenience.
     * Only this function will be called by the resolver in Purah.
     * It is not placed in FieldMatcher to avoid confusion.
     */

    Set<String> matchFields(Set<String> fields, Object belongInstance);

    default boolean match(String field, Object belongInstance) {
        return matchFields(Collections.singleton(field), belongInstance).contains(field);
    }

    default boolean match(String field) {
        return match(field, null);
    }

    default Set<String> matchFields(Set<String> fields) {
        return matchFields(fields, null);
    }

}
