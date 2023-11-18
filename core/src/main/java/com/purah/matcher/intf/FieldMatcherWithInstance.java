package com.purah.matcher.intf;

import java.util.Set;

public interface FieldMatcherWithInstance extends FieldMatcher {
    @Override
    default boolean match(String field) {

        return match(field, null);
    }

    @Override
    default Set<String> matchFields(Set<String> fields) {
        return this.matchFields(fields, null);
    }

    Set<String> matchFields(Set<String> fields, Object belongInstance);

    boolean match(String field, Object belongInstance);

}
