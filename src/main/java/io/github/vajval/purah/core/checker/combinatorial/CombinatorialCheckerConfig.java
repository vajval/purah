package io.github.vajval.purah.core.checker.combinatorial;


import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.Purahs;

import java.util.ArrayList;
import java.util.List;


/**
 * config for combo checkers
 */
public class CombinatorialCheckerConfig {

    final Purahs purahs;

    ExecMode.Main mainExecType = ExecMode.Main.all_success;

    String name;

    String logicFrom;

    ResultLevel resultLevel = ResultLevel.only_failed_only_base_logic;

    List<String> forRootInputArgCheckerNames = new ArrayList<>();

    protected int reOrderCount = -1;
    boolean autoLog = false;


    public final List<FieldMatcherCheckerConfig> fieldMatcherCheckerConfigList = new ArrayList<>();


    private CombinatorialCheckerConfig(Purahs purahs) {
        this.purahs = purahs;
    }


    public static CombinatorialCheckerConfig create(Purahs purahs) {
        return new CombinatorialCheckerConfig(purahs);
    }

    public String getLogicFrom() {
        return logicFrom;
    }

    public void setMainExecType(ExecMode.Main mainExecType) {
        this.mainExecType = mainExecType;
    }

    public void setForRootInputArgCheckerNames(List<String> forRootInputArgCheckerNames) {
        this.forRootInputArgCheckerNames = forRootInputArgCheckerNames;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMatcherCheckerName(FieldMatcher fieldMatcher, List<String> list) {
        fieldMatcherCheckerConfigList.add(new FieldMatcherCheckerConfig(fieldMatcher, list));
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

    public List<String> getForRootInputArgCheckerNames() {
        return forRootInputArgCheckerNames;
    }

    public int getReOrderCount() {
        return reOrderCount;
    }

    public void setReOrderCount(int reOrderCount) {
        this.reOrderCount = reOrderCount;
    }

    public boolean isAutoLog() {
        return autoLog;
    }

    public void setAutoLog(boolean autoLog) {
        this.autoLog = autoLog;
    }

    @Override
    public String toString() {
        return "CombinatorialCheckerConfig{" +
                ", mainExecType=" + mainExecType +
                ", name='" + name + '\'' +
                ", logicFrom='" + logicFrom + '\'' +
                ", resultLevel=" + resultLevel +
                ", extendCheckerNames=" + forRootInputArgCheckerNames +
                ", fieldMatcherCheckerConfigList=" + fieldMatcherCheckerConfigList +
                '}';
    }
}
