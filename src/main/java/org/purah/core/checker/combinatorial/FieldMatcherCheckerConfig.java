package org.purah.core.checker.combinatorial;


import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.matcher.FieldMatcher;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The correspondence between fields matched by fieldMatcher and the checkers to be used.
 */
public class FieldMatcherCheckerConfig {
    ExecMode.Matcher execType = ExecMode.Matcher.arg_checker;
    FieldMatcher fieldMatcher;
    List<String> checkerNames;


    private List<Checker<?,?>> checkers;

    public List<Checker<?,?>> getCheckers() {
        return checkers;
    }

    public void buildCheckers(CheckerManager checkerManager) {
        checkers = this.checkerNames.stream().map(checkerManager::of).collect(Collectors.toList());
    }

    public FieldMatcherCheckerConfig(FieldMatcher fieldMatcher, List<String> ruleNames) {
        this.fieldMatcher = fieldMatcher;
        this.checkerNames = ruleNames;
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