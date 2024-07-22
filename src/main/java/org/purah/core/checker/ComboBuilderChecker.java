package org.purah.core.checker;

import org.purah.core.PurahContext;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.LogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.MultiCheckResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.FieldMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*
   comboBuilderChecker = purahContext.combo().match(new GeneralFieldMatcher("id"), "id is 123")
   comboBuilderChecker.check(new User(123,bob));//true
   comboBuilderChecker.match(new GeneralFieldMatcher("name"), "name is alice")
   comboBuilderChecker.check(new User(123,bob));//false
   comboBuilderChecker.regSelf("123|alice")


   comboBuilderChecker =purahContext.combo("123|alice")
   comboBuilderChecker.check(new User(123,bob));//false
   comboBuilderChecker.check(new User(123,alice));//true
 */
public class ComboBuilderChecker implements Checker<Object, List<CheckResult<?>>> {


    final PurahContext purahContext;
    final CombinatorialCheckerConfig config;
    CombinatorialChecker combinatorialChecker;

    public ComboBuilderChecker(PurahContext purahContext, String... checkerNames) {
        this.purahContext = purahContext;
        config = CombinatorialCheckerConfig.create(purahContext);
        config.setForRootInputArgCheckerNames(Stream.of(checkerNames).collect(Collectors.toList()));
    }


    public ComboBuilderChecker match(FieldMatcher fieldMatcher, String... checkerNames) {
        config.addMatcherCheckerName(fieldMatcher, Stream.of(checkerNames).collect(Collectors.toList()));
        combinatorialChecker = null;
        return this;
    }

    public ComboBuilderChecker mainMode(ExecMode.Main mainMode) {
        config.setMainExecType(mainMode);
        combinatorialChecker = null;
        return this;

    }

    public ComboBuilderChecker resultLevel(ResultLevel resultLevel) {
        config.setResultLevel(resultLevel);
        combinatorialChecker = null;
        return this;

    }

    public GenericsProxyChecker regSelf(String name) {
        config.setName(name);
        CombinatorialChecker combinatorialChecker = new CombinatorialChecker(config);
        return purahContext.checkManager().reg(combinatorialChecker);
    }


    @Override
    public MultiCheckResult<CheckResult<?>> check(Object o) {
        return check(InputToCheckerArg.of(o));
    }

    @Override
    public MultiCheckResult<CheckResult<?>> check(InputToCheckerArg<Object> inputToCheckerArg) {
        if (combinatorialChecker != null) return combinatorialChecker.check(inputToCheckerArg);
        if (CollectionUtils.isEmpty(config.fieldMatcherCheckerConfigList)) {
            if (config.getForRootInputArgCheckerNames().size() == 0) {
                return new MultiCheckResult<>(LogicCheckResult.success(), Collections.emptyList());
            }
        }
        config.setLogicFrom("new ComboBuilderChecker() or purahContext.combo()");
        combinatorialChecker = new CombinatorialChecker(config);
        return combinatorialChecker.check(inputToCheckerArg);

    }
}
