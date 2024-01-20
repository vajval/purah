package com.purah.checker;


import com.purah.base.BaseManager;
import com.purah.exception.RuleRegException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则的管理器
 * 最好先注册字段规则
 * 再处理instance 规则
 */
public class CheckerManager {

    protected final Map<String, ExecChecker> cacheMap = new ConcurrentHashMap<>();


    public ExecChecker<?, ?> reg(Checker<?, ?> checker) {
        if (checker == null) {
            throw new RuleRegException("注册规则不能为空");
        }
        String name = checker.name();
        ExecChecker<?, ?> execChecker = cacheMap.get(name);
        if (execChecker == null) {
            execChecker = new ExecChecker<>(name);
            cacheMap.put(name, execChecker);
        }

        execChecker.addNewChecker(checker);
        System.out.println(checker.name());

        System.out.println(cacheMap);

        return execChecker;
    }


    public ExecChecker<?, ?> get(String name) {
        ExecChecker result = cacheMap.get(name);

        if (result == null) {
            throw new RuleRegException("未经注册的规则:" + name);
        }


//
        return result;
    }
}


//    public CheckerRule regRuleByChecker(Checker<?, ?> checker, String useName) {
//        if (checker == null) {
//            throw new RuleRegException("注册规则不能为空");
//        }
//        if (useName == null) {
//            useName = NameUtil.useName(checker);
//
//        }
//        Rule existRule = ruleMap.get(useName);
//        CheckerRule checkerRule;
//        if (existRule != null) {
//            if (!(existRule instanceof CheckerRule)) {
//                throw new RuleRegException("规则命名冲突:" + useName + "-" + checker.getClass());
//            }
//            checkerRule = ((CheckerRule) existRule);
//            checkerRule.addNewChecker(checker);
//        } else {
//            checkerRule = new CheckerRule(checker);
//            ruleMap.put(useName, checkerRule);
//        }
//        return checkerRule;
//    }
//
//    public CheckerRule regRuleByChecker(Checker<?, ?> checker) {
//
//        return this.regRuleByChecker(checker, null);
//
//
//    }
//
//    public void regRule(Rule rule, String... names) {
//        String useName = NameUtil.useName(rule);
//        if (ruleMap.containsKey(useName)) {
//            throw new RuleRegException("规则命名冲突:" + useName + "-" + rule.getClass());
//        }
//        ruleMap.put(useName, rule);
//    }
//
//
//    public Rule getRule(String name) {
//        Rule result = ruleMap.get(name);
//        if (result instanceof CombinatorialRule) {
//            CombinatorialRule combinatorialRule=    (CombinatorialRule) result;
//            combinatorialRule.init();
//        }
//        if (result == null) {
//            throw new RuleRegException("未经注册的规则:" + name);
//        }
//
//        return result;
//
//    }
//
//
//}
