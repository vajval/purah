package io.github.vajval.purah.spring.config;

import com.google.common.base.Splitter;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.ResultLevel;
//import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@ConfigurationProperties(value = "purah")
public class PurahConfigProperties {

    List<ChildComboProperties> comboChecker = Collections.emptyList();

    public List<ChildComboProperties> getComboChecker() {
        return comboChecker;
    }

    public void setComboChecker(List<ChildComboProperties> comboChecker) {
        this.comboChecker = comboChecker;
    }

    public List<CombinatorialCheckerConfigProperties> toCombinatorialCheckerConfigPropertiesList() {

        return comboChecker.stream().map(ChildComboProperties::toCombinatorialCheckerConfigProperties).collect(Collectors.toList());


    }


    private static List<String> split(String str) {
        if (!StringUtils.hasText(str)) return Collections.emptyList();
        return Splitter.on(",").splitToList(str);
    }

    static class ChildComboProperties {

        protected String name;

        protected int resultLevel = ResultLevel.only_failed_only_base_logic.value();

        protected int execType = ExecMode.Main.all_success.value();

        protected String checkers;
        protected int autoReOrder = -1;


        protected LinkedHashMap<String, LinkedHashMap<String, String>> mapping = new LinkedHashMap<>(0);


        public CombinatorialCheckerConfigProperties toCombinatorialCheckerConfigProperties() {
            CombinatorialCheckerConfigProperties result = new CombinatorialCheckerConfigProperties(name);
            result.setLogicFrom("PurahConfigProperties.CombinatorialCheckerProperties{" + this + "}");
            if (StringUtils.hasText(checkers)) {
                result.setUseCheckerNames(split(checkers));
            }
            for (Map.Entry<String, LinkedHashMap<String, String>> entry : mapping.entrySet()) {
                LinkedHashMap<String, List<String>> valueMap = new LinkedHashMap<>();
                for (Map.Entry<String, String> listEntry : entry.getValue().entrySet()) {
                    valueMap.put(listEntry.getKey(), Splitter.on(",").splitToList(listEntry.getValue()));
                }
                result.add(entry.getKey(), valueMap);
            }
            result.setResultLevel(ResultLevel.valueOf(resultLevel));
            result.setMainMode(ExecMode.Main.valueOf(execType));
            result.setReOrderCount(autoReOrder);
            return result;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCheckers() {
            return checkers;
        }

        public void setCheckers(String checkers) {
            this.checkers = checkers;
        }

        public int getAutoReOrder() {
            return autoReOrder;
        }

        public void setAutoReOrder(int autoReOrder) {
            this.autoReOrder = autoReOrder;
        }

        public int getResultLevel() {
            return resultLevel;
        }

        public void setResultLevel(int resultLevel) {
            this.resultLevel = resultLevel;
        }

        public int getExecType() {
            return execType;
        }

        public void setExecType(int execType) {
            this.execType = execType;
        }

        public LinkedHashMap<String, LinkedHashMap<String, String>> getMapping() {
            return mapping;
        }

        public void setMapping(LinkedHashMap<String, LinkedHashMap<String, String>> mapping) {
            if (mapping != null) {
                this.mapping = mapping;
            }
        }

        @Override
        public String toString() {
            return "CombinatorialCheckerProperties{" +
                    "name='" + name + '\'' +
                    ", useCheckers='" + checkers + '\'' +
                    ", execType=" + execType +
                    ", mapping=" + mapping +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PurahConfigProperties{" +
                "combinatorialCheckers=" + comboChecker +
                '}';
    }
}
