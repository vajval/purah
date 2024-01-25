package com.purah.checker.combinatorial;

import javax.annotation.Nonnull;
import java.util.*;

public class CombinatorialCheckerConfigProperties {
    String checkerName;

    ExecType.Main mainExecType=ExecType.Main.all_success;
    List<String> useCheckerNames = new ArrayList<>();
    LinkedHashMap<String, Map<String, String>> matcherFieldCheckerMapping = new LinkedHashMap<>();

    public CombinatorialCheckerConfigProperties(String checkerName) {
        this.checkerName = checkerName;
    }

    public List<String> getUseCheckerNames() {
        return useCheckerNames;
    }

    public void setUseCheckerNames(@Nonnull List<String> useCheckerNames) {
        this.useCheckerNames = useCheckerNames;
    }

    public CombinatorialCheckerConfigProperties add(String matchFactoryType, LinkedHashMap<String, String> fieldCheckerMapping) {
        matcherFieldCheckerMapping.put(matchFactoryType, fieldCheckerMapping);
        return this;

    }

    public ExecType.Main getMainExecType() {
        return mainExecType;
    }

    public void setMainExecType(ExecType.Main mainExecType) {
        this.mainExecType = mainExecType;
    }

    public String getCheckerName() {
        return checkerName;
    }



    public LinkedHashMap<String, Map<String, String>> getMatcherFieldCheckerMapping() {
        return matcherFieldCheckerMapping;
    }


}
