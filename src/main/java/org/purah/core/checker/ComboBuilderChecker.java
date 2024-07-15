package org.purah.core.checker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.PurahContext;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.matcher.FieldMatcher;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComboBuilderChecker extends AbstractBaseSupportCacheChecker<Object, Object> {

    private static final Logger logger = LogManager.getLogger(ComboBuilderChecker.class);

    PurahContext purahContext;
    CombinatorialCheckerConfig config;
    CombinatorialChecker combinatorialChecker;

    public ComboBuilderChecker(PurahContext purahContext) {
        this.purahContext = purahContext;
        config = CombinatorialCheckerConfig.create(purahContext);
    }

    public ComboBuilderChecker inputArg(String... checkerNames) {
        config.setExtendCheckerNames(Stream.of(checkerNames).collect(Collectors.toList()));
        combinatorialChecker = null;
        return this;
    }

    public ComboBuilderChecker match(FieldMatcher fieldMatcher, String... checkerNames) {
        config.addMatcherCheckerName(fieldMatcher, Stream.of(checkerNames).collect(Collectors.toList()));
        combinatorialChecker = null;

        return this;
    }


    @Override
    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {
        if (combinatorialChecker != null) return combinatorialChecker.check(inputToCheckerArg);
        if (CollectionUtils.isEmpty(config.fieldMatcherCheckerConfigList)) {
            if (config.extendCheckerNames.size() == 1) {
                return purahContext.checkManager().of(config.extendCheckerNames.get(0)).check(inputToCheckerArg);
            } else if (config.extendCheckerNames.size() == 0) {
                return BaseLogicCheckResult.success();
            }
        }
        combinatorialChecker = new CombinatorialChecker(config);
        return combinatorialChecker.check(inputToCheckerArg);

    }
}
