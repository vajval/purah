package com.purah.customSyntax;

import com.google.common.base.Splitter;
import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.combinatorial.ExecType;
import com.purah.checker.custom.AbstractCustomSyntaxCheckerFactory;
import com.purah.springboot.ann.EnableOnPurahContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;

@EnableOnPurahContext
@Component
public class CustomSyntaxCheckerFactory extends AbstractCustomSyntaxCheckerFactory {


    @Autowired
    PurahContext purahContext;


    @Override
    public PurahContext purahContext() {
        return purahContext;
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.startsWith("example:");
    }


    final static Splitter fieldSplitter = Splitter.on(";");
    final static Splitter iSplitter = Splitter.on(":");
    final static Splitter checkerSplitter = Splitter.on(",");

    /**
     * example: [a:b,c;e:d,e]
     *
     * @param needMatchCheckerName
     * @return
     */

    @Override
    public CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties(String needMatchCheckerName) {

        CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties = expToProperties(needMatchCheckerName);
        combinatorialCheckerConfigProperties.setLogicFrom(this.getClass().getName() + "|||" + needMatchCheckerName);
        return combinatorialCheckerConfigProperties;
    }

    public static CombinatorialCheckerConfigProperties expToProperties(String needMatchCheckerName) {


        // example: 0[x,y][a:b,c;e:d,e]
        String checkerExp = needMatchCheckerName.substring("example:".length()).trim();

        ExecType.Main mainExecType = ExecType.Main.valueOf(Integer.parseInt(String.valueOf(checkerExp.charAt(0))));

        // 0[x,y][a:b,c;e:d,e]


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties(needMatchCheckerName);

        properties.setMainExecType(mainExecType);
        properties.setIgnoreSuccessResult(false);
        // x,y
        String useCheckersExp = checkerExp.substring(checkerExp.indexOf("[") + 1, checkerExp.indexOf("]"));
        if (StringUtils.hasText(useCheckersExp)) {
            properties.setUseCheckerNames(checkerSplitter.splitToList(useCheckersExp));

        }


        // a:b,c;e:d,e
        String fieldCheckerExp = checkerExp.substring(checkerExp.lastIndexOf("[") + 1, checkerExp.lastIndexOf("]"));
        if (StringUtils.hasText(fieldCheckerExp)) {

            List<List<String>> list = fieldSplitter.splitToStream(fieldCheckerExp).
                    map(iSplitter::splitToList)
                    .toList();


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
