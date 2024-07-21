package org.purah.core.checker.factory;


import org.purah.core.PurahContext;
import org.purah.core.checker.Checker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;


/**
 * 自定义语法,单元测试有例子,可以根据起的名字动态生成checker
 * example: 0[x,y][a:b,c;e:d,e]    See unit test
 * Custom syntax, unit tests with examples, can dynamically generate a checker based on the given name.
 */
public abstract class AbstractCustomSyntaxCheckerFactory implements CheckerFactory {

    public abstract PurahContext purahContext();


    @Override
    public abstract boolean match(String needMatchCheckerName);


    public abstract CombinatorialCheckerConfigProperties combinatorialCheckerConfigProperties(String needMatchCheckerName);

    @Override
    public Checker<?,?> createChecker(String needMatchCheckerName) {
        PurahContext purahContext = purahContext();
        CombinatorialCheckerConfigProperties properties = combinatorialCheckerConfigProperties(needMatchCheckerName);
        String logicFrom = properties.getLogicFrom();
        if (logicFrom == null) {
            properties.setLogicFrom(this.getClass().getName());
        }
        return purahContext.createByProperties(properties);

    }
}
