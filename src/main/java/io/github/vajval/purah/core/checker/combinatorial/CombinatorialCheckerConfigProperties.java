package io.github.vajval.purah.core.checker.combinatorial;

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

    private boolean autoLog = false;

    private ExecMode.Main mainMode = ExecMode.Main.all_success;
    private List<String> useCheckerNames = new ArrayList<>();
    protected int reOrderCount = -1;
    private final LinkedHashMap<String, Map<String, List<String>>> matcherFieldCheckerMapping = new LinkedHashMap<>();


    public CombinatorialCheckerConfig buildToConfig(Purahs purahs) {

        CombinatorialCheckerConfig config = CombinatorialCheckerConfig.create(purahs);
        config.setAutoLog(this.autoLog);
        config.setMainExecType(this.getMainMode());
        config.setForRootInputArgCheckerNames(this.getUseCheckerNames());
        config.setName(this.getCheckerName());
        config.setResultLevel(this.getResultLevel());
        config.setLogicFrom(this.getLogicFrom());
        config.setReOrderCount(this.getReOrderCount());
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
            return "CombinatorialCheckerConfigProperties{" +
                    "checkerName='" + checkerName + '\'' +
                    ", mainExecType=" + mainMode +
                    ", useCheckerNames=" + useCheckerNames +
                    ", matcherFieldCheckerMapping=" + matcherFieldCheckerMapping +
                    '}';
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


    public boolean isAutoLog() {
        return autoLog;
    }

    public void setAutoLog(boolean autoLog) {
        this.autoLog = autoLog;
    }

    public int getReOrderCount() {
        return reOrderCount;
    }

    public void setReOrderCount(int reOrderCount) {
        this.reOrderCount = reOrderCount;
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
