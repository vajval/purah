package io.github.vajval.purah.core.checker.factory;


import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.ProxyChecker;


/**
 * See unit test    MyCustomSyntaxCheckerFactory
 * 自定义语法,单元测试有例子,可以根据起的名字动态生成checker
 * example: 0[x,y][a:b,c;e:d,e]    See unit test
 * Custom syntax, unit tests with examples, can dynamically generate a checker based on the given name.
 */
public abstract class AbstractCustomSyntaxCheckerFactory implements CheckerFactory {

    public abstract Purahs purahs();

    @Override
    public abstract boolean match(String needMatchCheckerName);

    public abstract Checker<?, ?> doCreateChecker(String needMatchCheckerName);

    public boolean cache(String needMatchCheckerName, Checker<?, ?> checker) {
        return true;
    }

    @Override
    public Checker<?, ?> createChecker(String needMatchCheckerName) {
        Checker<?, ?> checker = doCreateChecker(needMatchCheckerName);
        ProxyChecker result = new ProxyChecker(checker,needMatchCheckerName, this.getClass().getName());
        boolean cache = cache(needMatchCheckerName, checker);
        if (cache) {
            purahs().reg(result);
        }
        return result;
    }

}
