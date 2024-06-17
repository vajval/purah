package org.purah.core.checker.combinatorial;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.purah.core.PurahContext;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.matcher.FieldMatcher;

import javax.annotation.Nonnull;
import java.util.*;

public class CombinatorialCheckerConfigBuilder {
    private String checkerName;
    private ResultLevel resultLevel = ResultLevel.failedAndIgnoreNotBaseLogic;
    private String logicFrom;

    private ExecType.Main mainExecType = ExecType.Main.all_success;
    private List<String> useCheckerNames = new ArrayList<>();
    private LinkedHashMap<String, Map<String, List<String>>> matcherFieldCheckerMapping = new LinkedHashMap<>();


    public CombinatorialCheckerConfig build(PurahContext purahContext) {

        CombinatorialCheckerConfig config = CombinatorialCheckerConfig.create(purahContext);

        MatcherManager matcherManager = purahContext.matcherManager();


        config.setMainExecType(this.getMainExecType());
        config.setExtendCheckerNames(this.getUseCheckerNames());
        config.setName(this.getCheckerName());
        config.setResultLevel(this.getResultLevel());
        config.setLogicFrom(this.getLogicFrom());

        for (Map.Entry<String, Map<String, List<String>>> entry : this.getMatcherFieldCheckerMapping().entrySet()) {
            String matcherFactoryName = entry.getKey();
            MatcherFactory matcherFactory = matcherManager.factoryOf(matcherFactoryName);
            for (Map.Entry<String, List<String>> matcherStrChecker : entry.getValue().entrySet()) {
                String matcherStr = matcherStrChecker.getKey();
                FieldMatcher fieldMatcher = matcherFactory.create(matcherStr);
                List<String> checkerNameList = matcherStrChecker.getValue();
                config.addMatcherCheckerName(fieldMatcher, checkerNameList);
            }
        }
        return config;
    }


    public String getLogicFrom() {

        if (logicFrom == null) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("checkerName", checkerName);
            objectMap.put("useCheckerNames", useCheckerNames);
            objectMap.put("matcherFieldCheckerMapping", matcherFieldCheckerMapping);
            objectMap.put("resultLevel", resultLevel);
            objectMap.put("mainExecType", mainExecType);
            Gson gson = new Gson();
            String json = gson.toJson(objectMap);
            return "properties: " + json;
        }
        return logicFrom;
    }

    public void setLogicFrom(String logicFrom) {
        this.logicFrom = logicFrom;
    }

    public ResultLevel getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(ResultLevel resultLevel) {
        this.resultLevel = resultLevel;
    }

    public CombinatorialCheckerConfigBuilder(String checkerName) {
        this.checkerName = checkerName;
    }

    public List<String> getUseCheckerNames() {
        return useCheckerNames;
    }

    public void setUseCheckerNames(@Nonnull List<String> useCheckerNames) {
        this.useCheckerNames = useCheckerNames;
    }

    public CombinatorialCheckerConfigBuilder add(String matchFactoryType, LinkedHashMap<String, List<String>> fieldCheckerMapping) {
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

    public CombinatorialCheckerConfigBuilder addByStrMap(String matchFactoryType, LinkedHashMap<String, String> fieldCheckerStrMapping) {
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
