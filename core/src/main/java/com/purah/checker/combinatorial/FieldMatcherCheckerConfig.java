package com.purah.checker.combinatorial;

import com.purah.checker.Checker;
import com.purah.checker.CheckerManager;
import com.purah.checker.context.ExecType;
import com.purah.matcher.intf.FieldMatcher;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldMatcherCheckerConfig {
    ExecType.Matcher execType=ExecType.Matcher.instance_checker;
    FieldMatcher fieldMatcher;

    List<String> checkerNames;


    private List<Checker> checkers;

    public List<Checker> getCheckers() {
        return checkers;
    }

    public void buildCheckers(CheckerManager checkerManager) {
        checkers = this.checkerNames.stream().map(checkerManager::get).collect(Collectors.toList());
    }

    public FieldMatcherCheckerConfig(FieldMatcher fieldMatcher, List<String> ruleNames) {
        this.fieldMatcher = fieldMatcher;
        this.checkerNames = ruleNames;
    }
}