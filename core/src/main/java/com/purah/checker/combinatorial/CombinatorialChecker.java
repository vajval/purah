package com.purah.checker.combinatorial;

import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CombinatorialCheckerResult;
import com.purah.checker.context.ExecInfo;
import com.purah.checker.context.ExecType;
import com.purah.checker.context.SingleCheckerResult;

import java.util.List;
import java.util.function.Supplier;

public class CombinatorialChecker implements Checker<Object,Object> {



    @Override
    public SingleCheckerResult<Object> check(CheckInstance<Object> checkInstance) {
        return null;
    }

    //    @Override
//    public RuleResult check(Object checkInstance) {
//
//
//        RuleExec ruleExec = this.ruleExec();
//
//        List<Supplier<RuleResult>> supplierList = new ArrayList<>(mainExRuleList.size() + mainExRuleList.size());
//
//        for (Rule rule : mainExRuleList) {
//            supplierList.add(() -> rule.check(checkInstance));
//        }
//
//        for (MatcherExecRule matcherExecRule : matcherExecList) {
//            supplierList.add( () -> matcherExecRule.check(checkInstance));
//        }
//
//        ruleExec.exec(supplierList);
//        return ruleExec.result();
//    }
    public CombinatorialChecker(CombinatorialCheckerBuilder combinatorialCheckerBuilder) {

    }

//    static class Executor {
//        ExecType execType;
//        ExecInfo execInfo;
//        CombinatorialCheckerResult checkerResult;
//
//
//        public Executor(ExecType execType) {
//            this.execType = execType;
//            this.checkerResult = new CombinatorialCheckerResult();
//        }
//
//        public boolean exec(List<Supplier<CombinatorialCheckerResult>> ruleResultSupplierList) {
//            boolean result = true;
//            for (Supplier<CombinatorialCheckerResult> supplier : ruleResultSupplierList) {
//                CombinatorialCheckerResult ruleResult = supplier.get();
//                this.checkerResult.addOtherRuleResult(ruleResult);
//                if (ruleResult.haveError()) {
//                    return false;
//                }
//                if (ruleResult.haveFailed()) {
//                    if (execType == ExecType.Main.all_success) {
//                        return false;
//                    } else if (execType == ExecType.Main.all_success_but_must_check_all) {
//                        result = false;
//                    }
//                } else {
//                    if (execType == ExecType.Main.at_least_one) {
//                        return true;
//                    } else if (execType == ExecType.Main.at_least_one_but_must_check_all) {
//                        result = true;
//                    }
//                }
//            }
//            return result;
//
//
//        }
//
//        public RuleResult result() {
//            return ruleResult;
//        }
//    }

}

//    protected boolean init = false;
//
//    protected ArgResolverManager argResolverManager;
//
//    protected RuleManager ruleManager;
//
//
//    protected CombinatorialRuleConfig combinatorialRuleConfig;
//
//    protected List<Rule> mainExRuleList;
//
//    protected List<MatcherExecRule> matcherExecList;
//
//
//    public CombinatorialRule(CombinatorialRuleConfig combinatorialRuleConfig) {
//        this.combinatorialRuleConfig = combinatorialRuleConfig;
//        this.ruleManager = combinatorialRuleConfig.easyRuleContext.ruleManager();
//        this.argResolverManager = combinatorialRuleConfig.easyRuleContext.argResolverManager();
//
//
//    }
//

//
//
//    private RuleExec ruleExec() {
//        return new RuleExec(combinatorialRuleConfig.mainExecType);
//    }
//
//

//
//
//    public CombinatorialRule init() {
//        if (this.init) return this;
//        this.mainExRuleList = this.combinatorialRuleConfig.mainRuleNameList.stream().map(this::getAndInit).collect(Collectors.toList());
//        this.matcherExecList = this.combinatorialRuleConfig.matcherRuleConfigList.stream().map(MatcherExecRule::new).collect(Collectors.toList());
//        this.init = true;
//        return this;
//    }
//
//    public Rule getAndInit(String ruleName) {
//        Rule rule = ruleManager.getRule(ruleName);
//        if (rule instanceof CombinatorialRule) {
//            return ((CombinatorialRule) rule).init();
//        }
//        return rule;
//    }
//
//    class MatcherExecRule implements Rule {
//
//
//        FieldMatcher fieldMatcher;
//
//        ExecType.Matcher execType;
//
//        List<Rule> ruleList = new ArrayList<>();
//
//        public MatcherExecRule(CombinatorialRuleConfig.MatcherRuleConfig matcherRuleConfig) {
//            this.fieldMatcher = matcherRuleConfig.fieldMatcher;
//            this.execType = matcherRuleConfig.matcherExecType;
//            for (String ruleName : matcherRuleConfig.ruleNames) {
//                ruleList.add(CombinatorialRule.this.getAndInit(ruleName));
//            }
//        }
//
//        @Override
//        public RuleResult check(Object parentCheckInstance) {
//            ArgResolver argResolver = argResolverManager.getArgResolver(parentCheckInstance.getClass());
//            Map<String, Object> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(parentCheckInstance, fieldMatcher);
//
//            List<Supplier<RuleResult>> supplierList = new ArrayList<>();
//
//            if (execType == ExecType.Matcher.rule_instance) {
//                for (Rule rule : ruleList) {
//                    for (Map.Entry<String, Object> entry : matchFieldObjectMap.entrySet()) {
//                        supplierList.add(() -> rule.check(entry.getValue()));
//                    }
//                }
//            } else {
//                for (Map.Entry<String, Object> entry : matchFieldObjectMap.entrySet()) {
//                    for (Rule rule : ruleList) {
//                        supplierList.add(() -> rule.check(entry.getValue()));
//
//                    }
//                }
//            }
//            RuleExec ruleExec = CombinatorialRule.this.ruleExec();
//            ruleExec.exec(supplierList);
//            return ruleExec.result();
//        }
//    }
//
//    @Override
//    public String name() {
//        return combinatorialRuleConfig.ruleName;
//    }

