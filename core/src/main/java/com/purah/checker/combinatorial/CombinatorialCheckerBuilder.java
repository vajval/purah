package com.purah.checker.combinatorial;

import com.purah.checker.CheckerManager;
import com.purah.matcher.intf.FieldMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinatorialCheckerBuilder {
    public String name;

    public Map<FieldMatcher, List<String>> fieldMatcherListMap = new HashMap<>();

    private CombinatorialCheckerBuilder(String name) {
        this.name = name;
    }

    public static CombinatorialCheckerBuilder builder(String name) {
        return new CombinatorialCheckerBuilder(name);
    }

    public CombinatorialCheckerBuilder matcherCheckerName(FieldMatcher fieldMatcher, List<String> list) {
        fieldMatcherListMap.put(fieldMatcher, list);
        return this;
    }


    public CombinatorialChecker build(CheckerManager checkerManager) {

//        return new CombinatorialChecker();
    return null;
    }

}
