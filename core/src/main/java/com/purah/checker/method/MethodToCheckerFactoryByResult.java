package com.purah.checker.method;

import com.purah.base.PurahEnableMethod;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.matcher.singleLevel.WildCardMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

public class MethodToCheckerFactoryByResult implements CheckerFactory {


    WildCardMatcher wildCardMatcher;

    PurahEnableMethod purahEnableMethod;
    Method method;

    public MethodToCheckerFactoryByResult(Object bean, Method method, String matchStr) {
        this.wildCardMatcher = new WildCardMatcher(matchStr);
        purahEnableMethod = new PurahEnableMethod(bean, method, 1);
        this.method = method;
    }


    @Override
    public boolean match(String needMatchCheckerName) {
        return wildCardMatcher.match(needMatchCheckerName);
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {


        return new BaseChecker() {
            @Override
            public CheckerResult doCheck(CheckInstance checkInstance) {

                Object[] args = new Object[2];
                args[0] = needMatchCheckerName;
                args[1] = purahEnableMethod.checkInstanceToInputArg(checkInstance);


                return purahEnableMethod.invoke(args);
            }

            @Override
            public String logicFrom() {

                return method.toGenericString();
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
