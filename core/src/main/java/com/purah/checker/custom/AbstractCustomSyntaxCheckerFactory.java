package com.purah.checker.custom;

import com.purah.PurahContext;
import com.purah.checker.Checker;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.checker.factory.CheckerFactory;

public abstract class AbstractCustomSyntaxCheckerFactory implements CheckerFactory {

    public abstract PurahContext purahContext();


    @Override
    public abstract boolean match(String needMatchCheckerName);


    public abstract CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties(String needMatchCheckerName);

    @Override
    public Checker createChecker(String needMatchCheckerName) {

        PurahContext purahContext = purahContext();

        CombinatorialCheckerConfigProperties properties = combinatorialCheckerConfigProperties(needMatchCheckerName);

        return purahContext.createNewCombinatorialChecker(properties);

    }
}
