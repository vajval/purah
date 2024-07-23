package org.purah.core.checker.combinatorial;


import org.purah.core.Purahs;
import org.purah.core.checker.Checker;
import org.purah.core.matcher.FieldMatcher;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The correspondence between fields matched by fieldMatcher and the checkers to be used.
 */
public class FieldMatcherCheckerConfig {
    final ExecMode.Matcher execType = ExecMode.Matcher.arg_checker;
    final FieldMatcher fieldMatcher;
    final List<String> checkerNames;

    private List<Checker<?,?>> checkers;

    public List<Checker<?,?>> getCheckers() {
        return checkers;
    }

    public void buildCheckers(Purahs purahs) {
        checkers = this.checkerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());
    }

    public FieldMatcherCheckerConfig(FieldMatcher fieldMatcher, List<String> checkerNames) {
        this.fieldMatcher = fieldMatcher;
        this.checkerNames = checkerNames;
    }

    @Override
    public String toString() {
        return "FieldMatcherCheckerConfig{" +
                "execType=" + execType +
                ", fieldMatcher=" + fieldMatcher +
                ", checkerNames=" + checkerNames +
                ", checkers=" + checkers +
                '}';
    }
}