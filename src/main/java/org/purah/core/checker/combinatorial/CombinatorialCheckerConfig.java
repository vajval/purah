package org.purah.core.checker.combinatorial;


import org.purah.core.PurahContext;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.intf.FieldMatcher;

import java.util.ArrayList;
import java.util.List;

public class CombinatorialCheckerConfig {

    PurahContext purahContext;

    ExecType.Main mainExecType = ExecType.Main.all_success;

    public String name;

    public String logicFrom;

    public ResultLevel resultLevel ;

    public List<String> extendCheckerNames = new ArrayList<>();

    public List<FieldMatcherCheckerConfig> fieldMatcherCheckerConfigList = new ArrayList<>();


    private CombinatorialCheckerConfig(PurahContext purahContext) {
        this.purahContext = purahContext;
    }


    public static CombinatorialCheckerConfig create(PurahContext purahContext) {
        return new CombinatorialCheckerConfig(purahContext);
    }

    public String getLogicFrom() {
        return logicFrom;
    }

    public void setMainExecType(ExecType.Main mainExecType) {
        this.mainExecType = mainExecType;
    }

    public void setExtendCheckerNames(List<String> extendCheckerNames) {
        this.extendCheckerNames = extendCheckerNames;

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
}
