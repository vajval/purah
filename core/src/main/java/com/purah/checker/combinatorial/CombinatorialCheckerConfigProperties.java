package com.purah.checker.combinatorial;

import com.google.common.base.Splitter;

import javax.annotation.Nonnull;
import java.util.*;

public class CombinatorialCheckerConfigProperties {
    String checkerName;
    boolean ignoreSuccessResult=true;
    public String logicFrom;



    ExecType.Main mainExecType = ExecType.Main.all_success;
    List<String> useCheckerNames = new ArrayList<>();
    LinkedHashMap<String, Map<String, List<String>>> matcherFieldCheckerMapping = new LinkedHashMap<>();


    public String getLogicFrom() {
        return logicFrom;
    }

    public void setLogicFrom(String logicFrom) {
        this.logicFrom = logicFrom;
    }

    public boolean isIgnoreSuccessResult() {
        return ignoreSuccessResult;
    }

    public void setIgnoreSuccessResult(boolean ignoreSuccessResult) {
        this.ignoreSuccessResult = ignoreSuccessResult;
    }

    public CombinatorialCheckerConfigProperties(String checkerName) {
        this.checkerName = checkerName;
    }

    public List<String> getUseCheckerNames() {
        return useCheckerNames;
    }

    public void setUseCheckerNames(@Nonnull List<String> useCheckerNames) {
        this.useCheckerNames = useCheckerNames;
    }

    public CombinatorialCheckerConfigProperties add(String matchFactoryType, LinkedHashMap<String, List<String>> fieldCheckerMapping) {
        LinkedHashMap<String, List<String>> valueMap = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : fieldCheckerMapping.entrySet()) {
            ArrayList<String> checkerLists = new ArrayList<>(entry.getValue().size());
            for (String checkerName : entry.getValue()) {
                checkerLists.add(checkerName.trim());
            }
            valueMap.put(entry.getKey().trim(), checkerLists);
        }

        matcherFieldCheckerMapping.put(matchFactoryType.trim(), valueMap);
        return this;

    }

    public CombinatorialCheckerConfigProperties addByStrMap(String matchFactoryType, LinkedHashMap<String, String> fieldCheckerStrMapping) {
        LinkedHashMap<String, List<String>> valueMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : fieldCheckerStrMapping.entrySet()) {
            ArrayList<String> checkerLists = new ArrayList<>();
            for (String checkerName : Splitter.on(",").splitToList(entry.getValue().trim())) {
                checkerLists.add(checkerName.trim());
            }
            valueMap.put(entry.getKey().trim(), checkerLists);
        }

        matcherFieldCheckerMapping.put(matchFactoryType.trim(), valueMap);
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


    public LinkedHashMap<String, Map<String, List<String>>> getMatcherFieldCheckerMapping() {
        return matcherFieldCheckerMapping;
    }

    @Override
    public String toString() {
        return "CombinatorialCheckerConfigProperties{" +
                "checkerName='" + checkerName + '\'' +
                ", mainExecType=" + mainExecType +
                ", useCheckerNames=" + useCheckerNames +
                ", matcherFieldCheckerMapping=" + matcherFieldCheckerMapping +
                '}';
    }
}
