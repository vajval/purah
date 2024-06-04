package org.purah.example.customSyntax;

import com.google.common.base.Splitter;

import org.purah.core.PurahContext;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigBuilder;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.checker.custom.AbstractCustomSyntaxCheckerFactory;
import org.purah.springboot.ann.EnableBeanOnPurahContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@EnableBeanOnPurahContext
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
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.startsWith("example:");
    }




    /**
     * example: [a:b,c;e:d,e]
     *
     * @param needMatchCheckerName
     * @return
     */

    @Override
    public CombinatorialCheckerConfigBuilder combinatorialCheckerConfigProperties(String needMatchCheckerName) {

        CombinatorialCheckerConfigBuilder combinatorialCheckerConfigBuilder = expToProperties(needMatchCheckerName);
        combinatorialCheckerConfigBuilder.setLogicFrom("[" + needMatchCheckerName + "]" + "[" + this.getClass().getName() + "]");
        return combinatorialCheckerConfigBuilder;
    }

    public static CombinatorialCheckerConfigBuilder expToProperties(String needMatchCheckerName) {


        // example: 0[x,y][a:b,c;e:d,e]
        String checkerExp = needMatchCheckerName.substring("example:".length()).trim();

        ExecType.Main mainExecType = ExecType.Main.valueOf(Integer.parseInt(String.valueOf(checkerExp.charAt(0))));

        // 0[x,y][a:b,c;e:d,e]


        CombinatorialCheckerConfigBuilder properties = new CombinatorialCheckerConfigBuilder(needMatchCheckerName);

        properties.setMainExecType(mainExecType);
        properties.setResultLevel(ResultLevel.failedAndIgnoreNotBaseLogic);
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
