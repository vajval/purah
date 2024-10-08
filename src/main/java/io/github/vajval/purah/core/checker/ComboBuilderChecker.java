package io.github.vajval.purah.core.checker;

import com.google.common.collect.Lists;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialChecker;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.exception.UnexpectedException;
import io.github.vajval.purah.core.matcher.FieldMatcher;
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


    private final Purahs purahs;
    private final CombinatorialCheckerConfig config;
    private CombinatorialChecker combinatorialChecker;

    public ComboBuilderChecker(Purahs purahs, String... checkerNames) {
        this.purahs = purahs;
        config = CombinatorialCheckerConfig.create(purahs);
        config.setName("create by combo");
        config.setForRootInputArgCheckerNames(Stream.of(checkerNames).collect(Collectors.toList()));
        config.setLogicFrom("create by combo");
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

    public ComboBuilderChecker autoReOrder(int count) {
        config.setReOrderCount(count);
        combinatorialChecker = null;
        return this;

    }

    public ComboBuilderChecker resultLevel(ResultLevel resultLevel) {
        config.setResultLevel(resultLevel);
        combinatorialChecker = null;
        return this;

    }

    public ComboBuilderChecker thisName(String name) {
        config.setName(name);
        combinatorialChecker = null;
        return this;
    }

    @Override
    public MultiCheckResult<CheckResult<?>> oCheck(Object o) {
        return check(InputToCheckerArg.of(o));
    }

    public GenericsProxyChecker regSelf(String name) {
        config.setName(name);
        CombinatorialChecker combinatorialChecker = new CombinatorialChecker(config);
        return purahs.reg(combinatorialChecker);
    }


    @Override
    public MultiCheckResult<CheckResult<?>> check(InputToCheckerArg<Object> inputToCheckerArg) {
        if (combinatorialChecker != null) return combinatorialChecker.check(inputToCheckerArg);
        if (CollectionUtils.isEmpty(config.fieldMatcherCheckerConfigList)) {
            if (config.getForRootInputArgCheckerNames().size() == 0) {
                return new MultiCheckResult<>(LogicCheckResult.success(), Collections.emptyList());
            }
            if (config.getForRootInputArgCheckerNames().size() == 1) {
                String singleCheckerName = config.getForRootInputArgCheckerNames().get(0);
                return singleCheck(singleCheckerName, inputToCheckerArg);
            }
        }
        config.setLogicFrom("new ComboBuilderChecker() or purahContext.combo()");
        combinatorialChecker = new CombinatorialChecker(config);
        return combinatorialChecker.check(inputToCheckerArg);

    }


    private MultiCheckResult<CheckResult<?>> singleCheck(String singleCheckerName, InputToCheckerArg<Object> inputToCheckerArg) {
        Checker<Object, Object> checker = purahs.checkerOf(singleCheckerName);
        CheckResult<Object> childResult = checker.check(inputToCheckerArg);
        if (childResult.isSuccess()) {
            return new MultiCheckResult<>(LogicCheckResult.success(), Lists.newArrayList(childResult));
        } else if (childResult.isFailed()) {
            return new MultiCheckResult<>(LogicCheckResult.failed(singleCheckerName + " failed"), Lists.newArrayList(childResult));
        }
        throw new UnexpectedException();
    }
}
