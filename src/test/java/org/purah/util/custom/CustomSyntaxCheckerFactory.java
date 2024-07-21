package org.purah.util.custom;

import com.google.common.base.Splitter;

import org.purah.core.PurahContext;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.checker.factory.AbstractCustomSyntaxCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class CustomSyntaxCheckerFactory extends AbstractCustomSyntaxCheckerFactory {


    @Autowired
    PurahContext purahContext;

    final static Splitter fieldSplitter = Splitter.on(";");
    final static Splitter iSplitter = Splitter.on(":");
    final static Splitter checkerSplitter = Splitter.on(",");


    @Override
    public PurahContext purahContext() {
        return purahContext;
    }


    @Override
    public String name() {
        return "example";
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.startsWith("example:");
    }


    /**
     * example: [a:b,c;e:d,e]
     *
     */

    @Override
    public CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties(String needMatchCheckerName) {

        CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties = expToProperties(needMatchCheckerName);
        combinatorialCheckerConfigProperties.setLogicFrom("[" + needMatchCheckerName + "]" + "[" + this.getClass().getName() + "]");
        return combinatorialCheckerConfigProperties;
    }

    public static CombinatorialCheckerConfigProperties expToProperties(String needMatchCheckerName) {


        // example: 0[x,y][a:b,c;e:d,e]
        String checkerExp = needMatchCheckerName.substring("example:".length()).trim();

        ExecMode.Main mainExecType = ExecMode.Main.valueOf(Integer.parseInt(String.valueOf(checkerExp.charAt(0))));

        // 0[x,y][a:b,c;e:d,e]


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties(needMatchCheckerName);

        properties.setMainMode(mainExecType);
        properties.setResultLevel(ResultLevel.only_failed_only_base_logic);
        // x,y
        String useCheckersExp = checkerExp.substring(checkerExp.indexOf("[") + 1, checkerExp.indexOf("]"));
        if (StringUtils.hasText(useCheckersExp)) {
            properties.setUseCheckerNames(checkerSplitter.splitToList(useCheckersExp));

        }


        // a:b,c;e:d,e
        String fieldCheckerExp = checkerExp.substring(checkerExp.lastIndexOf("[") + 1, checkerExp.lastIndexOf("]"));
        if (StringUtils.hasText(fieldCheckerExp)) {

            List<List<String>> list = fieldSplitter.splitToList(fieldCheckerExp).stream().
                    map(iSplitter::splitToList)
                    .collect(Collectors.toList());


            LinkedHashMap<String, List<String>> fieldCheckerMap = new LinkedHashMap<>();


            for (List<String> strList : list) {
                String fieldStr = strList.get(0);
                String checkersStr = strList.get(1);
                fieldCheckerMap.put(fieldStr, checkerSplitter.splitToList(checkersStr));

            }


            properties.add("general", fieldCheckerMap);
        }


        return properties;
    }


}
