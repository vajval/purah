package com.purah;

import com.google.common.base.Splitter;
import com.purah.checker.Checker;
import com.purah.checker.CheckerManager;
import com.purah.checker.ExecChecker;
import com.purah.checker.combinatorial.CombinatorialChecker;
import com.purah.checker.combinatorial.CombinatorialCheckerConfig;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.matcher.MatcherManager;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.resolver.ArgResolverManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class PurahContext {


    private CheckerManager checkManager = new CheckerManager();

    private ArgResolverManager argResolverManager = new ArgResolverManager();

    private MatcherManager matcherManager = new MatcherManager();

    public CheckerManager checkManager() {
        return checkManager;

    }

    public ArgResolverManager argResolverManager() {
        return argResolverManager;
    }

    public MatcherManager matcherManager() {
        return matcherManager;
    }


    public CombinatorialChecker combinatorialOf(String... checkerNames) {
        List<String> checkerNameList = Stream.of(checkerNames).toList();

        CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties = new CombinatorialCheckerConfigProperties(UUID.randomUUID().toString());
        combinatorialCheckerConfigProperties.setUseCheckerNames(checkerNameList);
        combinatorialCheckerConfigProperties.setLogicFrom("PurahContext.combinatorialOf" + checkerNameList);
        return createNewCombinatorialChecker(combinatorialCheckerConfigProperties);

    }

    public CombinatorialChecker createNewCombinatorialChecker(CombinatorialCheckerConfigProperties properties) {


        CombinatorialCheckerConfig config = CombinatorialCheckerConfig.create(this);
        config.setMainExecType(properties.getMainExecType());
        config.setExtendCheckerNames(properties.getUseCheckerNames());
        config.setName(properties.getCheckerName());
        config.setIgnoreSuccessResult(properties.isIgnoreSuccessResult());
        for (Map.Entry<String, Map<String, List<String>>> entry : properties.getMatcherFieldCheckerMapping().entrySet()) {
            String matcherFactoryName = entry.getKey();
            MatcherFactory matcherFactory = matcherManager.factoryOf(matcherFactoryName);
            for (Map.Entry<String, List<String>> matcherStrChecker : entry.getValue().entrySet()) {
                String matcherStr = matcherStrChecker.getKey();
                FieldMatcher fieldMatcher = matcherFactory.create(matcherStr);
                List<String> checkerNameList = matcherStrChecker.getValue();
                config.addMatcherCheckerName(fieldMatcher, checkerNameList);
            }
        }
        return new CombinatorialChecker(config);
    }

    public Checker regNewCombinatorialChecker(CombinatorialCheckerConfigProperties properties) {
        Checker newCombinatorialChecker = createNewCombinatorialChecker(properties);
        return checkManager.reg(newCombinatorialChecker);
    }


}
