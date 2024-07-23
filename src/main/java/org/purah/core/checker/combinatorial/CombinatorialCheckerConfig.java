package org.purah.core.checker.combinatorial;


import org.purah.core.PurahContext;
import org.purah.core.Purahs;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.FieldMatcher;

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

     ResultLevel resultLevel=ResultLevel.only_failed_only_base_logic;

     List<String> forRootInputArgCheckerNames = new ArrayList<>();

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
