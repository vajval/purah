package io.github.vajval.purah.spring.extra;

import com.google.common.base.Splitter;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.ComboBuilderChecker;
import io.github.vajval.purah.core.checker.factory.AbstractCustomSyntaxCheckerFactory;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


/*
 * 自定义语法解析器
 * see org.purah.springboot.aop.AspectTestService
 */

public class ExampleCustomSyntaxCheckerFactory extends AbstractCustomSyntaxCheckerFactory {


    Purahs purahs;

    final static Splitter fieldSplitter = Splitter.on(";");
    final static Splitter iSplitter = Splitter.on(":");
    final static Splitter checkerSplitter = Splitter.on(",");

    public ExampleCustomSyntaxCheckerFactory(Purahs purahs) {
        this.purahs = purahs;
    }

    public Purahs purahs() {
        return purahs;
    }

    @Override
    public String name() {
        return "example";
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.startsWith("example:");
    }

    /*
     * example: [a:b,c;e:d,e]
     */
    @Override
    public Checker<Object, List<CheckResult<?>>> doCreateChecker(String needMatchCheckerName) {


        // example: 0[x,y][a:b,c;e:d,e]
        String checkerExp = needMatchCheckerName.substring("example:".length()).trim();

        ExecMode.Main mainExecType = ExecMode.Main.valueOf(Integer.parseInt(String.valueOf(checkerExp.charAt(0))));

        // 0[x,y][a:b,c;e:d,e]


        // x,y
        String useCheckersExp = checkerExp.substring(checkerExp.indexOf("[") + 1, checkerExp.indexOf("]"));
        String[] rootCheckerNames = checkerSplitter.splitToList(useCheckersExp).stream().filter(StringUtils::hasText).toArray(String[]::new);
        ComboBuilderChecker checker = purahs().combo(rootCheckerNames)
                .resultLevel(ResultLevel.only_failed_only_base_logic)
                .mainMode(mainExecType);

        // a:b,c;e:d,e
        String fieldCheckerExp = checkerExp.substring(checkerExp.lastIndexOf("[") + 1, checkerExp.lastIndexOf("]"));

        if (StringUtils.hasText(fieldCheckerExp)) {
            List<List<String>> list = fieldSplitter.splitToList(fieldCheckerExp).stream().
                    map(iSplitter::splitToList)
                    .collect(Collectors.toList());

            for (List<String> strList : list) {
                String fieldStr = strList.get(0);
                String[] checkerNames = checkerSplitter.splitToList(strList.get(1)).stream().filter(StringUtils::hasText).toArray(String[]::new);
                checker.match(new GeneralFieldMatcher(fieldStr), checkerNames);
            }

        }
        checker.name(needMatchCheckerName);
        return checker;
    }


}
