package io.github.vajval.purah.core;


import io.github.vajval.purah.core.checker.CheckerManager;
import io.github.vajval.purah.core.checker.converter.DefaultMethodConverter;
import io.github.vajval.purah.core.checker.converter.MethodConverter;
import io.github.vajval.purah.core.matcher.MatcherManager;
import io.github.vajval.purah.core.resolver.ArgResolver;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;

/**
 * 核心上下文
 * not use this
 * plz use Purahs purahContext.purahs() or new Purahs(purahContext) 效
 */

public class PurahContext {

    public static final MethodConverter DEFAULT_METHOD_CONVERTER = new DefaultMethodConverter();
    private final PurahContextConfig config;
    private CheckerManager checkManager = new CheckerManager();
    private ArgResolver argResolver = new ReflectArgResolver();
    private MatcherManager matcherManager = new MatcherManager();
    private MethodConverter enableMethodConverter = DEFAULT_METHOD_CONVERTER;

    public PurahContext(PurahContextConfig config) {
        this.config = config;
        config().purahDefaultFieldMatcherClass().forEach(matcherManager::regBaseStrMatcher);
    }

    public PurahContext() {
        this(new PurahContextConfig());
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


    public Purahs purahs() {
        return new Purahs(this);
    }

    public void clearAll() {
        matcherManager.clear();
        checkManager.clear();
        if (argResolver.getClass().equals(ReflectArgResolver.class)) {
            ((ReflectArgResolver) argResolver).clearCache();
        }
    }

}
