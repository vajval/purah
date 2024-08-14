package io.github.vajval.purah.core;

import io.github.vajval.purah.core.checker.combinatorial.CombinatorialChecker;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import io.github.vajval.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.ComboBuilderChecker;
import io.github.vajval.purah.core.checker.GenericsProxyChecker;
import io.github.vajval.purah.core.checker.converter.MethodConverter;
import io.github.vajval.purah.core.checker.factory.CheckerFactory;
import io.github.vajval.purah.core.matcher.factory.BaseMatcherFactory;
import io.github.vajval.purah.core.matcher.factory.MatcherFactory;
import io.github.vajval.purah.core.resolver.ArgResolver;

public class Purahs {
    final PurahContext purahContext;

    public Purahs(PurahContext purahContext) {
        this.purahContext = purahContext;
    }

    public ComboBuilderChecker combo(String... checkerNames) {
        return new ComboBuilderChecker(this, checkerNames);
    }

    public Checker<Object, Object> checkerOf(String name) {
        GenericsProxyChecker result = purahContext.checkManager().of(name);
        if (result == null) {
            throw new InitCheckerException("checker no reg and cannot be matched :[  " + name + "  ]");
        }
        return result;
    }

    public CombinatorialChecker checkerOf(CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties) {
        CombinatorialCheckerConfig config = combinatorialCheckerConfigProperties.buildToConfig(this);
        return new CombinatorialChecker(config);
    }


    public MatcherFactory matcherOf(String name) {
        MatcherFactory matcherFactory = purahContext.matcherManager().factoryOf(name);
        if (matcherFactory == null) {
            throw new RuntimeException("no reg MatcherFactory: " + name);
        }
        return matcherFactory;
    }

    public GenericsProxyChecker reg(CombinatorialCheckerConfigProperties properties) {
        Checker<?, ?> newCombinatorialChecker = checkerOf(properties);
        return purahContext.checkManager().reg(newCombinatorialChecker);
    }

    public GenericsProxyChecker reg(Checker<?, ?> checker) {
        return purahContext.checkManager().reg(checker);
    }

    public void reg(MatcherFactory matcherFactory) {
        purahContext.matcherManager().reg(matcherFactory);


    }

    public void reg(CheckerFactory checkerFactory) {
        purahContext.checkManager().addCheckerFactory(checkerFactory);
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
