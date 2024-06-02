package org.purah.core;


import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigBuilder;
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

    public PurahContextConfig config;


    private CheckerManager checkManager = new CheckerManager();

    private ArgResolverManager argResolverManager = new ArgResolverManager();

    private MatcherManager matcherManager = new MatcherManager();

    public PurahContext(PurahContextConfig config) {
        this.config = config;
    }

    public PurahContext() {
        this(new PurahContextConfig());
    }

    public PurahContextConfig config() {
        return config;
    }

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

        CombinatorialCheckerConfigBuilder combinatorialCheckerConfigBuilder = new CombinatorialCheckerConfigBuilder(UUID.randomUUID().toString());
        combinatorialCheckerConfigBuilder.setUseCheckerNames(checkerNameList);
        combinatorialCheckerConfigBuilder.setLogicFrom("PurahContext.combinatorialOf" + checkerNameList);
        return createNewCombinatorialChecker(combinatorialCheckerConfigBuilder);

    }

    public CombinatorialChecker createNewCombinatorialChecker(CombinatorialCheckerConfigBuilder builder) {
        CombinatorialCheckerConfig config = builder.build(this);

        return new CombinatorialChecker(config);

    }

    public Checker regNewCombinatorialChecker(CombinatorialCheckerConfigBuilder properties) {
        Checker newCombinatorialChecker = createNewCombinatorialChecker(properties);
        return checkManager.reg(newCombinatorialChecker);
    }


}
