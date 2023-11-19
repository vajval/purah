package com.purah.checker.combinatorial;

import com.purah.checker.CheckerManager;
import com.purah.matcher.factory.MatcherFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CombinatorialCheckerConfigProperties {
    String checkerName;

    List<String> extendCheckerNames = new ArrayList<>();
    Map<String, Map<String, String>> matcherFieldCheckerMapping = new ConcurrentHashMap<>();

    public CombinatorialCheckerConfigProperties(String checkerName) {
        this.checkerName = checkerName;
    }

    public CombinatorialCheckerConfigProperties extend(List<String> extendCheckerNames) {
        this.extendCheckerNames = extendCheckerNames;
        return this;

    }

    public CombinatorialCheckerConfigProperties add(String matchFactoryType, Map<String, String> fieldCheckerMapping) {
        matcherFieldCheckerMapping.put(matchFactoryType, fieldCheckerMapping);
        return this;

    }

    public CombinatorialChecker build(MatcherFactory matcherManager, CheckerManager checkerManager) {

//        CombinatorialCheckerBuilder.builder(checkerName).matcherCheckerName()
//        CombinatorialCheckerBuilder combinatorialCheckerBuilder = new CombinatorialCheckerBuilder();
//        return null;
        return null;
    }


}
