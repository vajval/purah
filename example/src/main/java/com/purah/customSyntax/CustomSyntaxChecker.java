package com.purah.customSyntax;

import com.google.common.base.Splitter;
import com.purah.PurahContext;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.custom.AbstractCustomSyntaxChecker;
import com.purah.springboot.ann.EnableOnPurahContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;

@EnableOnPurahContext
@Component
public class CustomSyntaxChecker extends AbstractCustomSyntaxChecker {


    @Autowired
    PurahContext purahContext;


    @Override
    public PurahContext purahContext() {
        System.out.println(purahContext);
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

        return expToProperties(needMatchCheckerName);
    }

    public static CombinatorialCheckerConfigProperties expToProperties(String needMatchCheckerName) {


        // example: [x,y][a:b,c;e:d,e]
        String checkerExp = needMatchCheckerName.substring("example:".length()).trim();
        // [x,y][a:b,c;e:d,e]


        CombinatorialCheckerConfigProperties properties = new CombinatorialCheckerConfigProperties(needMatchCheckerName);
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
