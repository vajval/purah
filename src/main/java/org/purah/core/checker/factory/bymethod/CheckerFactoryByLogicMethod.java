package org.purah.core.checker.factory.bymethod;


import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.method.PurahEnableMethod;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

import java.lang.reflect.Method;
import java.util.Objects;

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
            public CheckResult doCheck(InputCheckArg inputCheckArg) {

                Object[] args = new Object[2];
                args[0] = needMatchCheckerName;
                args[1] = purahEnableMethod.checkInstanceToInputArg(inputCheckArg);
                Object result = purahEnableMethod.invoke(args);

                if (purahEnableMethod.resultIsCheckResultClass()) {
                    return (CheckResult) result;
                } else if (Objects.equals(result, true)) {
                    return success(inputCheckArg, true);
                } else if (Objects.equals(result, false)) {
                    return failed(inputCheckArg, false);
                }
                throw new RuntimeException("不阿盖出错");
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
