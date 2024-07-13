package org.purah.core;


import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.converter.DefaultMethodConverter;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import org.purah.core.matcher.singlelevel.ClassNameMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.singlelevel.ReMatcher;
import org.purah.core.matcher.singlelevel.WildCardMatcher;
import org.purah.core.resolver.ArgResolver;
import org.purah.core.resolver.DefaultArgResolver;

public class PurahContext {

    public static final MethodConverter DEFAULT_METHOD_CONVERTER = new DefaultMethodConverter();


    private final PurahContextConfig config;

    private CheckerManager checkManager = new CheckerManager();

    private ArgResolver argResolver = new DefaultArgResolver();

    private MatcherManager matcherManager = new MatcherManager();

    private MethodConverter enableMethodConverter = DEFAULT_METHOD_CONVERTER;

    public PurahContext(PurahContextConfig config) {
        this.config = config;
        this.regBaseStringMatcher();
    }

    public PurahContext() {
        this(new PurahContextConfig());
    }


    private void regBaseStringMatcher() {

        matcherManager.regBaseStrMatcher(AnnTypeFieldMatcher.class);
        matcherManager.regBaseStrMatcher(ClassNameMatcher.class);
        matcherManager.regBaseStrMatcher(ReMatcher.class);
        matcherManager.regBaseStrMatcher(WildCardMatcher.class);
        matcherManager.regBaseStrMatcher(GeneralFieldMatcher.class);
        config().getBaseStringMatcherClass().forEach(matcherManager::regBaseStrMatcher);

    }

    public void override(CheckerManager checkManager, ArgResolver argResolver, MatcherManager matcherManager, MethodConverter enableMethodConverter) {
        if (checkManager != null) {
            this.checkManager = checkManager;

        }
        if (argResolver != null) {
            this.argResolver = argResolver;

        }
        if (matcherManager != null) {
            this.matcherManager = matcherManager;
            this.regBaseStringMatcher();
        }
        if (enableMethodConverter != null) {
            this.enableMethodConverter = enableMethodConverter;
        }
    }


    public PurahContextConfig config() {
        return config;
    }

    public CheckerManager checkManager() {
        return checkManager;

    }

    public MethodConverter enableMethodConverter() {
        return enableMethodConverter;
    }

    public ArgResolver argResolver() {
        return argResolver;
    }

    public MatcherManager matcherManager() {
        return matcherManager;
    }


    public ComboBuilderChecker combo(String... checkerNames) {
        ComboBuilderChecker comboBuilderChecker = new ComboBuilderChecker(this);
        comboBuilderChecker.inputArg(checkerNames);
        return comboBuilderChecker;

    }

    public CombinatorialChecker createNewCombinatorialChecker(CombinatorialCheckerConfigProperties builder) {
        CombinatorialCheckerConfig config = builder.build(this);

        return new CombinatorialChecker(config);

    }

    public Checker<?, ?> regNewCombinatorialChecker(CombinatorialCheckerConfigProperties properties) {
        Checker newCombinatorialChecker = createNewCombinatorialChecker(properties);
        return checkManager.reg(newCombinatorialChecker);
    }


}
