package com.purah.springboot.config;

import com.google.common.base.Splitter;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.combinatorial.ExecType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(value = "purah")
public class PurahConfigProperties {

    List<CombinatorialCheckerProperties> combinatorialCheckers = Collections.emptyList();


    public List<CombinatorialCheckerProperties> getCombinatorialCheckers() {
        return combinatorialCheckers;
    }

    public void setCombinatorialCheckers(List<CombinatorialCheckerProperties> combinatorialCheckers) {
        this.combinatorialCheckers = combinatorialCheckers;
    }

    public List<CombinatorialCheckerConfigProperties> toCombinatorialCheckerConfigPropertiesList() {

        return combinatorialCheckers.stream().map(CombinatorialCheckerProperties::toCombinatorialCheckerConfigProperties).collect(Collectors.toList());


    }


    private static List<String> split(String str) {
        if (!StringUtils.hasText(str)) return Collections.emptyList();
        return Splitter.on(",").splitToList(str);
    }

    static class CombinatorialCheckerProperties {

        protected String name;
        protected String useCheckers = "";
        protected int execType = ExecType.Main.all_success.value();

        protected LinkedHashMap<String, LinkedHashMap<String, String>> mapping = new LinkedHashMap<>(0);


        public CombinatorialCheckerConfigProperties toCombinatorialCheckerConfigProperties() {
            CombinatorialCheckerConfigProperties result = new CombinatorialCheckerConfigProperties(name);

            result.setUseCheckerNames(split(useCheckers));
            for (Map.Entry<String, LinkedHashMap<String, String>> entry : mapping.entrySet()) {
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

        public String getUseCheckers() {
            return useCheckers;
        }

        public void setUseCheckers(String useCheckers) {
            if (useCheckers != null) {
                this.useCheckers = useCheckers;
            }

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
                    ", useCheckers='" + useCheckers + '\'' +
                    ", execType=" + execType +
                    ", mapping=" + mapping +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PurahConfigProperties{" +
                "combinatorialCheckers=" + combinatorialCheckers +
                '}';
    }
}
