package com.purah.checker.combinatorial;

import com.purah.checker.CheckerManager;
import com.purah.matcher.factory.MatcherFactory;

import java.util.HashMap;
import java.util.Map;

public class CombinatorialCheckerConfigProperties {
    String checkerName;
    Map<String, Map<String, String>> matcherFieldCheckerMapping = new HashMap<>();

    public CombinatorialCheckerConfigProperties(String checkerName) {
        this.checkerName = checkerName;
    }

    public CombinatorialCheckerConfigProperties add(String matchFactoryType, Map<String, String> fieldCheckerMapping) {
        matcherFieldCheckerMapping.put(matchFactoryType, fieldCheckerMapping);
        return this;

    }
    public CombinatorialChecker build(MatcherFactory matcherManager, CheckerManager checkerManager) {
return null;
    }


}
