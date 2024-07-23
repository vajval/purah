package org.purah.core;

import org.purah.core.checker.Checker;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.factory.BaseMatcherFactory;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.resolver.ArgResolver;

public class Purahs {
    PurahContext purahContext;

    public Purahs(PurahContext purahContext) {
        this.purahContext = purahContext;
    }

    public ComboBuilderChecker combo(String... checkerNames) {
        return new ComboBuilderChecker(this, checkerNames);

    }

    public Checker<Object, Object> checkerOf(String name) {
        return purahContext.checkManager().of(name);

    }

    public CombinatorialChecker checkerOf(CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties) {
        CombinatorialCheckerConfig config = combinatorialCheckerConfigProperties.buildToConfig(this);
        return new CombinatorialChecker(config);
    }


    public MatcherFactory matcherOf(String name) {
        return purahContext.matcherManager().factoryOf(name);
    }

    public GenericsProxyChecker reg(CombinatorialCheckerConfigProperties properties) {
        Checker<?, ?> newCombinatorialChecker = checkerOf(properties);
        return purahContext.checkManager().reg(newCombinatorialChecker);
    }

    public GenericsProxyChecker reg(Checker<?, ?> checker) {
        return purahContext.checkManager().reg(checker);
    }

    public MatcherFactory reg(MatcherFactory matcherFactory) {
        purahContext.matcherManager().reg(matcherFactory);
        return matcherFactory;


    }

    public CheckerFactory reg(CheckerFactory checkerFactory) {
        purahContext.checkManager().addCheckerFactory(checkerFactory);
        return checkerFactory;
    }

    public MethodConverter methodConverter() {
        return purahContext.enableMethodConverter();
    }

    public BaseMatcherFactory reg(Class<? extends FieldMatcher> clazz) {
        return purahContext.matcherManager().regBaseStrMatcher(clazz);
    }

    public ArgResolver argResolver() {
        return purahContext.argResolver();
    }


}
