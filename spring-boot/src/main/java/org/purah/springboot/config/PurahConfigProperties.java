package org.purah.springboot.config;

import com.google.common.base.Splitter;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.combinatorial.ExecType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(value = "purah")
public class PurahConfigProperties {

    List<CombinatorialCheckerProperties> rules = Collections.emptyList();


    public List<CombinatorialCheckerProperties> getRules() {
        return rules;
    }

    public void setRules(List<CombinatorialCheckerProperties> rules) {
        this.rules = rules;
    }


    public List<CombinatorialCheckerConfigProperties> toCombinatorialCheckerConfigPropertiesList() {

        return rules.stream().map(CombinatorialCheckerProperties::toCombinatorialCheckerConfigProperties).collect(Collectors.toList());


    }


    private static List<String> split(String str) {
        if (!StringUtils.hasText(str)) return Collections.emptyList();
        return Splitter.on(",").splitToList(str);
    }

    static class CombinatorialCheckerProperties {

        protected String name;
        protected String checkers;
        protected int execType;

        protected Map<String, Map<String, String>> mapping;


        public CombinatorialCheckerConfigProperties toCombinatorialCheckerConfigProperties() {
            CombinatorialCheckerConfigProperties result = new CombinatorialCheckerConfigProperties(name);

            result.setUseCheckerNames(split(checkers));
            for (Map.Entry<String, Map<String, String>> entry : mapping.entrySet()) {
                result.add(entry.getKey(), new LinkedHashMap<>(entry.getValue()));
            }
            result.setMainExecType(ExecType.Main.valueOf(execType));
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

        public int getExecType() {
            return execType;
        }

        public void setExecType(int execType) {
            this.execType = execType;
        }

        public Map<String, Map<String, String>> getMapping() {
            return mapping;
        }

        public void setMapping(Map<String, Map<String, String>> mapping) {
            this.mapping = mapping;
        }
    }

}
