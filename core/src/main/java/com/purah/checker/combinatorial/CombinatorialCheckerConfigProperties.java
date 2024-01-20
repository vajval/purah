package com.purah.checker.combinatorial;

import com.google.common.base.Splitter;
import com.purah.checker.CheckerManager;
import com.purah.matcher.MatcherManager;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombinatorialCheckerConfigProperties {
    String checkerName;

    List<String> extendCheckerNames = new ArrayList<>();
    LinkedHashMap<String, Map<String, String>> matcherFieldCheckerMapping = new LinkedHashMap<>();

    public CombinatorialCheckerConfigProperties(String checkerName) {
        this.checkerName = checkerName;
    }

    public CombinatorialCheckerConfigProperties extend(List<String> extendCheckerNames) {
        this.extendCheckerNames = extendCheckerNames;
        return this;

    }

    public CombinatorialCheckerConfigProperties add(String matchFactoryType, LinkedHashMap<String, String> fieldCheckerMapping) {
        matcherFieldCheckerMapping.put(matchFactoryType, fieldCheckerMapping);
        return this;

    }

    public String checkerName() {
        return checkerName;
    }

    public List<String> extendCheckerNames() {
        return extendCheckerNames;
    }

    public LinkedHashMap<String, Map<String, String>> matcherFieldCheckerMapping() {
        return matcherFieldCheckerMapping;
    }


}
