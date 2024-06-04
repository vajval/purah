package org.purah.core.checker.factory.bymethod;


import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.Method;

public class CheckerFactoryByLogicMethod implements CheckerFactory {


    WildCardMatcher wildCardMatcher;

    PurahEnableMethod purahEnableMethod;
    boolean cacheBeCreatedChecker;

    public CheckerFactoryByLogicMethod(Object bean, Method method, String matchStr, boolean cacheBeCreatedChecker) {
        this.wildCardMatcher = new WildCardMatcher(matchStr);
        purahEnableMethod = new PurahEnableMethod(bean, method, 1);
        this.cacheBeCreatedChecker = cacheBeCreatedChecker;

    }


    @Override
    public boolean cacheBeCreatedChecker() {
        return this.cacheBeCreatedChecker;
    }

    @Override
    public boolean match(String needMatchCheckerName) {
        return wildCardMatcher.match(needMatchCheckerName);
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {


        return new BaseSupportCacheChecker() {
            @Override
            public CheckResult doCheck(CheckInstance checkInstance) {

                Object[] args = new Object[2];
                args[0] = needMatchCheckerName;
                args[1] = purahEnableMethod.checkInstanceToInputArg(checkInstance);


                return purahEnableMethod.invoke(args);
            }

            @Override
            public String logicFrom() {

                return purahEnableMethod.wrapperMethod().toGenericString();
            }

            @Override
            public Class<?> inputCheckInstanceClass() {
                return purahEnableMethod.needCheckArgClass();
            }

            @Override
            public Class<?> resultClass() {
                return purahEnableMethod.resultWrapperClass();
            }

            @Override
            public String name() {
                return needMatchCheckerName;
            }
        };
    }


}
