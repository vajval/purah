package io.github.vajval.purah.core.checker.combinatorial;


import io.github.vajval.purah.core.matcher.FieldMatcher;

import java.util.List;


/**
 * The correspondence between fields matched by fieldMatcher and the checkers to be used.
 */
public class FieldMatcherCheckerConfig {
    final FieldMatcher fieldMatcher;
    final List<String> checkerNames;


    public FieldMatcherCheckerConfig(FieldMatcher fieldMatcher, List<String> checkerNames) {
        this.fieldMatcher = fieldMatcher;
        this.checkerNames = checkerNames;
    }

    @Override
    public String toString() {
        return "FieldMatcherCheckerConfig{" +
                "fieldMatcher=" + fieldMatcher +
                ", checkerNames=" + checkerNames +
                '}';
    }
}