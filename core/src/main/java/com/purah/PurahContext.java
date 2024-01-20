package com.purah;

import com.google.common.base.Splitter;
import com.purah.checker.Checker;
import com.purah.checker.CheckerManager;
import com.purah.checker.ExecChecker;
import com.purah.checker.combinatorial.CombinatorialChecker;
import com.purah.checker.combinatorial.CombinatorialCheckerConfig;
import com.purah.checker.combinatorial.CombinatorialCheckerConfigProperties;
import com.purah.matcher.MatcherManager;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.resolver.ArgResolverManager;

import java.util.List;
import java.util.Map;

public class PurahContext {


    private CheckerManager checkManager = new CheckerManager();

    private ArgResolverManager argResolverManager = new ArgResolverManager();

    private MatcherManager matcherManager = new MatcherManager();

    public CheckerManager checkManager() {
        return checkManager;

    }

    public ArgResolverManager argResolverManager() {
        return argResolverManager;
    }

    public MatcherManager matcherManager() {
        return matcherManager;
    }


    public Checker regNewCombinatorialChecker(CombinatorialCheckerConfigProperties properties) {


        CombinatorialCheckerConfig config = CombinatorialCheckerConfig.create(this);
        config.setExtendCheckerNames(properties.extendCheckerNames());
        config.setName(properties.checkerName());
        for (Map.Entry<String, Map<String, String>> entry : properties.matcherFieldCheckerMapping().entrySet()) {
            String matcherFactoryName = entry.getKey();
            MatcherFactory matcherFactory = matcherManager.factoryOf(matcherFactoryName);
            for (Map.Entry<String, String> matcherStrChecker : entry.getValue().entrySet()) {
                String matcherStr = matcherStrChecker.getKey();
                FieldMatcher fieldMatcher = matcherFactory.create(matcherStr);
                String checkerNameListStr = matcherStrChecker.getValue();
                List<String> checkerNameList = Splitter.on(",").splitToList(checkerNameListStr);
                config.addMatcherCheckerName(fieldMatcher, checkerNameList);
            }
        }
        CombinatorialChecker checker = new CombinatorialChecker(config);
        Checker result = checkManager.reg(checker);
        return result;
    }


}


//    public boolean checkWithThreadCache(Object checkInstance, Class<? extends RuleCaches> clazz, String... ruleNames) {
//
//
//        RuleCacheContext open = RuleCacheContext.open();
//
//        for (String ruleName : ruleNames) {
//            Rule rule = ruleManager.getRule(ruleName);
//
//
//            if (rule.check(checkInstance).haveFailed()) {
//                return false;
//            }
//
//        }
//        return true;
//    }
////
//
//    public boolean checkWithThreadCache(Object checkInstance, String... ruleNames) {
//        RuleCacheContext open = RuleCacheContext.open();
//
//        for (String ruleName : ruleNames) {
//            Rule rule = ruleManager.getRule(ruleName);
//            if (rule.check(checkInstance).haveFailed()) {
//                return false;
//            }
//
//        }
//        return true;
//    }

//    public boolean check(Object checkInstance, String... ruleNames) {
//        for (String ruleName : ruleNames) {
//            Rule rule = ruleManager.getRule(ruleName);
//            RuleResult check = rule.check(checkInstance);
//
//            if (rule.check(checkInstance).haveFailed()) {
//                return false;
//            }
//
//        }
//        return true;
//
//    }
//
//
//}
