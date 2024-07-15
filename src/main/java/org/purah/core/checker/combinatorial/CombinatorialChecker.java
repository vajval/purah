package org.purah.core.checker.combinatorial;


import org.purah.core.name.IName;
import org.purah.core.checker.*;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.CombinatorialCheckResult;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.resolver.ArgResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
 * 配置的多个规则像结合形成的规则
 * purah:
 *  combo_checker:
 *    - name: user_reg
 *      mapping:
 *         wild_card:
 *             "[{address,parent_address}]": national_check
 *             "[*name*]": name_validity_check
 *             "[age]": age_check
 *         package_ann:
 *             "org.myCompany.myModule":validating_nested_parameters_for_custom_annotations
 *         class_name:
 *             "String":sensitive_word_check
 */
public class CombinatorialChecker extends AbstractBaseSupportCacheChecker<Object, Object> {


    CombinatorialCheckerConfig config;


    /**
     * checkers for inputArg
     */
    protected List<Checker> rootInputArgCheckers;
    /**
     * checkers for inputArg matched fields
     */
    public List<FieldMatcherCheckerConfig> fieldMatcherCheckerConfigList = new ArrayList<>();
    private boolean init = false;

    public CombinatorialChecker(CombinatorialCheckerConfig config) {
        this.config = config;
    }


    @Override
    public String logicFrom() {
        return config.getLogicFrom();
    }

    @Override
    public String name() {
        return config.name;
    }

    public CombinatorialChecker init() {
        if (this.init) return this;
        CheckerManager checkerManager = config.purahContext.checkManager();
        this.rootInputArgCheckers = this.config.extendCheckerNames.stream().map(checkerManager::of).collect(Collectors.toList());
        this.fieldMatcherCheckerConfigList = config.fieldMatcherCheckerConfigList;
        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : this.fieldMatcherCheckerConfigList) {
            fieldMatcherCheckerConfig.buildCheckers(checkerManager);
        }
        this.init = true;
        return this;
    }

    @Override
    public CheckResult doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        if (!init) {
            init();
        }


        MultiCheckerExecutor executor = createMultiCheckerExecutor();


        /*
           check inputArg
         */
        for (Checker checker : rootInputArgCheckers) {
            executor.add(inputToCheckerArg, checker);
        }
        /*
         * check  inputArg matched fields
         */

        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : fieldMatcherCheckerConfigList) {
            FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor = new FieldMatcherCheckerConfigExecutor(fieldMatcherCheckerConfig);
//            executor.add(() -> fieldMatcherCheckerConfigExecutor.check(inputToCheckerArg));
            fieldMatcherCheckerConfigExecutor.addToMultiCheckerExecutor(executor, inputToCheckerArg);


        }
        String log = "[" + inputToCheckerArg.fieldStr() + "]: " + this.name();
        CombinatorialCheckResult result = executor.toCombinatorialCheckResult(log);
        result.setCheckLogicFrom(this.logicFrom());
        return result;

    }


    public ArgResolver getArgResolverManager() {
        return config.purahContext.argResolver();
    }

    private MultiCheckerExecutor createMultiCheckerExecutor() {
        return new MultiCheckerExecutor(this.config.mainExecType, this.config.resultLevel);
    }


    class FieldMatcherCheckerConfigExecutor {
        FieldMatcherCheckerConfig fieldMatcherCheckerConfig;
        List<Checker> checkerList;


        public FieldMatcherCheckerConfigExecutor(FieldMatcherCheckerConfig fieldMatcherCheckerConfig) {
            checkerList = fieldMatcherCheckerConfig.getCheckers();
            this.fieldMatcherCheckerConfig = fieldMatcherCheckerConfig;
        }

        protected List<Supplier<CheckResult<?>>> checkResultSupplierList(InputToCheckerArg<Object> inputToCheckerArg) {
            ArgResolver argResolver = getArgResolverManager();
            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcherCheckerConfig.fieldMatcher);
            ExecType.Matcher execType = fieldMatcherCheckerConfig.execType;
            List<Supplier<CheckResult<?>>> result = new ArrayList<>(checkerList.size() * matchFieldObjectMap.size());
            if (execType == ExecType.Matcher.checker_arg) {
                for (Checker checker : checkerList) {
                    for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
                        result.add(() -> checker.check(entry.getValue()));
                    }
                }
            } else if (execType == ExecType.Matcher.arg_checker) {
                for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
                    for (Checker checker : checkerList) {
                        result.add(() -> checker.check(entry.getValue()));
                    }
                }
            } else {
                throw new UnexpectedException("execType:[" + execType + "]" + "  " + Arrays.toString(ExecType.Matcher.values()));
            }

            return result;
        }

        public void addToMultiCheckerExecutor(MultiCheckerExecutor executor, InputToCheckerArg<Object> inputToCheckerArg) {
            List<Supplier<CheckResult<?>>> suppliers = checkResultSupplierList(inputToCheckerArg);
            for (Supplier<CheckResult<?>> supplier : suppliers) {
                executor.add(supplier);
            }
        }

        public CheckResult check(InputToCheckerArg<Object> inputToCheckerArg) {
            MultiCheckerExecutor multiCheckerExecutor = CombinatorialChecker.this.createMultiCheckerExecutor();
            addToMultiCheckerExecutor(multiCheckerExecutor, inputToCheckerArg);

            String checkerNamesStr = fieldMatcherCheckerConfig.getCheckers().stream().map(IName::name).collect(Collectors.joining(","));
            FieldMatcher fieldMatcher = fieldMatcherCheckerConfig.fieldMatcher;

            String log = inputToCheckerArg.fieldStr() + " match:(" + fieldMatcher + ") checkers: " + checkerNamesStr;
            CombinatorialCheckResult result = multiCheckerExecutor.toCombinatorialCheckResult(log);

            result.setCheckLogicFrom("combinatorial by config,see log");
            return result;
        }
    }


}


