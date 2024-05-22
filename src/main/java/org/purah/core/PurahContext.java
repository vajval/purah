package org.purah.core;


import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.resolver.ArgResolverManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
        List<String> checkerNameList = Stream.of(checkerNames).collect(Collectors.toList());

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
        config.setResultLevel(ResultLevel.valueOf(properties.getResultLevel()));
        config.setLogicFrom(properties.getLogicFrom());

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
