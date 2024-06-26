package org.purah.core.checker.factory;


import org.purah.core.PurahContext;
import org.purah.core.checker.Checker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigBuilder;

public abstract class AbstractCustomSyntaxCheckerFactory implements CheckerFactory {

    public abstract PurahContext purahContext();


    @Override
    public abstract boolean match(String needMatchCheckerName);


    public abstract CombinatorialCheckerConfigBuilder combinatorialCheckerConfigProperties(String needMatchCheckerName);

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        PurahContext purahContext = purahContext();
        CombinatorialCheckerConfigBuilder properties = combinatorialCheckerConfigProperties(needMatchCheckerName);
        String logicFrom = properties.getLogicFrom();
        if (logicFrom == null) {
            properties.setLogicFrom(this.getClass().getName());
        }
        return purahContext.createNewCombinatorialChecker(properties);

    }
}
