package org.purah.core;


import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.converter.DefaultMethodConverter;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.matcher.MatcherManager;
import org.purah.core.resolver.ArgResolver;
import org.purah.core.resolver.DefaultArgResolver;

/**
 * 核心上下文
 * not use this
 * plz use Purahs purahContext.purahs() or new Purahs(purahContext) 效
 */

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


        config().purahDefaultFieldMatcherClass().forEach(matcherManager::regBaseStrMatcher);

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

    protected CheckerManager checkManager() {
        return checkManager;

    }

    public MethodConverter enableMethodConverter() {
        return enableMethodConverter;
    }

    public ArgResolver argResolver() {
        return argResolver;
    }

    protected MatcherManager matcherManager() {
        return matcherManager;
    }




    public Purahs purahs() {
        return new Purahs(this);
    }


}
