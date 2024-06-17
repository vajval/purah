package org.purah.core;


import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigBuilder;
import org.purah.core.checker.factory.method.converter.DefaultMethodToCheckerFactoryConverter;
import org.purah.core.checker.factory.method.converter.MethodToCheckerFactoryConverter;
import org.purah.core.checker.method.converter.DefaultMethodToCheckerConverter;
import org.purah.core.checker.method.converter.MethodToCheckerConverter;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.clazz.ClassNameMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.ReMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.purah.core.resolver.ArgResolverManager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurahContext {

    public static final MethodToCheckerConverter DEFAULT_METHOD_TO_CHECKER_CONVERTER = new DefaultMethodToCheckerConverter();
    public static final MethodToCheckerFactoryConverter DEFAULT_METHOD_TO_CHECKER_FACTORY_CONVERTER = new DefaultMethodToCheckerFactoryConverter();


    public PurahContextConfig config;


    private CheckerManager checkManager = new CheckerManager();

    private ArgResolverManager argResolverManager = new ArgResolverManager();

    private MatcherManager matcherManager = new MatcherManager();

    public PurahContext(PurahContextConfig config) {
        this.config = config;
        this.regBaseStringMatcher();
    }

    public PurahContext() {
        this(new PurahContextConfig());
    }

    public void overrideMatcherManager(MatcherManager matcherManager) {
        this.matcherManager = matcherManager;
        this.regBaseStringMatcher();

    }

    public void regBaseStringMatcher() {

        matcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        matcherManager.regBaseStrMatcher(ClassNameMatcher.class);
        matcherManager.regBaseStrMatcher(ReMatcher.class);
        matcherManager.regBaseStrMatcher(WildCardMatcher.class);
        matcherManager.regBaseStrMatcher(GeneralFieldMatcher.class);
        config().getBaseStringMatcherClass().forEach(matcherManager::regBaseStrMatcher);

    }

    public void overrideCheckerManager(CheckerManager checkManager) {
        this.checkManager = checkManager;

    }

    public void overrideArgResolverManager(ArgResolverManager argResolverManager) {
        this.argResolverManager = argResolverManager;

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

    public Checker<?, ?> regNewCombinatorialChecker(CombinatorialCheckerConfigBuilder properties) {
        Checker newCombinatorialChecker = createNewCombinatorialChecker(properties);
        return checkManager.reg(newCombinatorialChecker);
    }


}
