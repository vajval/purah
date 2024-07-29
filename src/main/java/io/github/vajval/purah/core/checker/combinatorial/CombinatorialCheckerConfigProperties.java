package io.github.vajval.purah.core.checker.combinatorial;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.matcher.factory.MatcherFactory;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * config builder
 */
public class CombinatorialCheckerConfigProperties {
    private final String checkerName;
    private ResultLevel resultLevel = ResultLevel.only_failed_only_base_logic;
    private String logicFrom;

    private ExecMode.Main mainMode = ExecMode.Main.all_success;
    private List<String> useCheckerNames = new ArrayList<>();
    private final LinkedHashMap<String, Map<String, List<String>>> matcherFieldCheckerMapping = new LinkedHashMap<>();


    public CombinatorialCheckerConfig buildToConfig(Purahs purahs) {

        CombinatorialCheckerConfig config = CombinatorialCheckerConfig.create(purahs);

//        Purahs purahs = purahContext.purahs();


        config.setMainExecType(this.getMainMode());
        config.setForRootInputArgCheckerNames(this.getUseCheckerNames());
        config.setName(this.getCheckerName());
        config.setResultLevel(this.getResultLevel());
        config.setLogicFrom(this.getLogicFrom());

        for (Map.Entry<String, Map<String, List<String>>> entry : this.getMatcherFieldCheckerMapping().entrySet()) {
            String matcherFactoryName = entry.getKey();
            MatcherFactory matcherFactory = purahs.matcherOf(matcherFactoryName);
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
            objectMap.put("mainExecType", mainMode);
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

    public CombinatorialCheckerConfigProperties(String checkerName) {
        this.checkerName = checkerName;
    }

    public List<String> getUseCheckerNames() {
        return useCheckerNames;
    }

    public void setUseCheckerNames(@Nonnull List<String> useCheckerNames) {
        this.useCheckerNames = useCheckerNames;
    }

    public void add(String matchFactoryType, LinkedHashMap<String, List<String>> fieldCheckerMapping) {
        LinkedHashMap<String, List<String>> valueMap = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : fieldCheckerMapping.entrySet()) {
            ArrayList<String> checkerLists = new ArrayList<>(entry.getValue().size());
            for (String checkerName : entry.getValue()) {
                checkerLists.add(checkerName.trim());
            }
            valueMap.put(entry.getKey().trim(), checkerLists);
        }

        matcherFieldCheckerMapping.put(matchFactoryType.trim(), valueMap);

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

    public ExecMode.Main getMainMode() {
        return mainMode;
    }

    public void setMainMode(ExecMode.Main mainMode) {
        this.mainMode = mainMode;
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
                ", mainExecType=" + mainMode +
                ", useCheckerNames=" + useCheckerNames +
                ", matcherFieldCheckerMapping=" + matcherFieldCheckerMapping +
                '}';
    }
}
