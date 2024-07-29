package org.purah.core.checker.combinatorial;


import org.purah.core.Purahs;
import org.purah.core.checker.result.MultiCheckResult;
import org.purah.core.checker.*;
import org.purah.core.checker.result.CheckResult;
//import org.purah.core.checker.result.CombinatorialCheckResult;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.resolver.ArgResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
 * purah:
 *  combo_checker:
 *    - name: user_reg
 *      mapping:
 *         wild_card:
 *             "[address|parent_address]": national_check
 *             "[*name*]": name_validity_check
 *             "[age]": age_check
 *         package_ann:
 *             "org.myCompany.myModule":validating_nested_parameters_for_custom_annotations
 *         class_name:
 *             "String":sensitive_word_check
 */
public class CombinatorialChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {


    final CombinatorialCheckerConfig config;

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

    @Override
    public MultiCheckResult<CheckResult<?>> check(InputToCheckerArg<Object> inputToCheckerArg) {
        return (MultiCheckResult) super.check(inputToCheckerArg);
    }

    @Override
    public MultiCheckResult<CheckResult<?>> check(Object o) {
        return (MultiCheckResult) super.check(o);
    }

    @Override
    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {


        MultiCheckerExecutor executor = new MultiCheckerExecutor(this.config.mainExecType, this.config.resultLevel);


        /*
           check inputArg
         */
        Purahs purahs = config.purahs;
        List<Checker<?, ?>> rootInputArgCheckers = this.config.forRootInputArgCheckerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());
        for (Checker<?, ?> checker : rootInputArgCheckers) {
            executor.add(inputToCheckerArg, checker);
        }
        /*
         * check  inputArg matched field values
         */

        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : config.fieldMatcherCheckerConfigList) {
            FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor = new FieldMatcherCheckerConfigExecutor(purahs, fieldMatcherCheckerConfig);
            fieldMatcherCheckerConfigExecutor.addToMultiCheckerExecutor(executor, inputToCheckerArg);

        }
        String log = "[" + inputToCheckerArg.fieldPath() + "]: " + this.name();
        MultiCheckResult<CheckResult<?>> multiCheckResult = executor.toMultiCheckResult(log);
        multiCheckResult.setCheckLogicFrom(this.logicFrom());
        return multiCheckResult;

    }



    class FieldMatcherCheckerConfigExecutor {
        final FieldMatcherCheckerConfig fieldMatcherCheckerConfig;
        final List<Checker<?, ?>> checkerList;

        public FieldMatcherCheckerConfigExecutor(Purahs purahs, FieldMatcherCheckerConfig fieldMatcherCheckerConfig) {
            this.fieldMatcherCheckerConfig = fieldMatcherCheckerConfig;
            this.checkerList = fieldMatcherCheckerConfig.checkerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());

        }

        protected List<Supplier<CheckResult<?>>> checkResultSupplierList(InputToCheckerArg<Object> inputToCheckerArg) {
            ArgResolver argResolver = config.purahs.argResolver();
            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcherCheckerConfig.fieldMatcher);
            ExecMode.Matcher execType = fieldMatcherCheckerConfig.execType;

            List<Supplier<CheckResult<?>>> result = new ArrayList<>(checkerList.size() * matchFieldObjectMap.size());
            if (execType == ExecMode.Matcher.checker_arg) {
                for (Checker<?, ?> checker : checkerList) {
                    for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
                        InputToCheckerArg<?> fieldArg = entry.getValue();
                        result.add(() -> ((Checker) checker).check(fieldArg));
                    }
                }
            } else if (execType == ExecMode.Matcher.arg_checker) {
                for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
                    for (Checker<?, ?> checker : checkerList) {
                        InputToCheckerArg<?> fieldArg = entry.getValue();
                        result.add(() -> ((Checker) checker).check(fieldArg));
                    }
                }
            } else {
                throw new UnexpectedException("execType:[" + execType + "]" + "  " + Arrays.toString(ExecMode.Matcher.values()));
            }

            return result;
        }

        public void addToMultiCheckerExecutor(MultiCheckerExecutor executor, InputToCheckerArg<Object> inputToCheckerArg) {
            List<Supplier<CheckResult<?>>> suppliers = checkResultSupplierList(inputToCheckerArg);
            for (Supplier<CheckResult<?>> supplier : suppliers) {
                executor.add(supplier);
            }
        }

//        public MultiCheckResult<CheckResult<?>> check(InputToCheckerArg<Object> inputToCheckerArg) {
//            MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(CombinatorialChecker.this.config.mainExecType, CombinatorialChecker.this.config.resultLevel);
//            addToMultiCheckerExecutor(multiCheckerExecutor, inputToCheckerArg);
//
//            String checkerNamesStr = checkerList.stream().map(IName::name).collect(Collectors.joining(","));
//            FieldMatcher fieldMatcher = fieldMatcherCheckerConfig.fieldMatcher;
//
//            String log = inputToCheckerArg.fieldPath() + " match:(" + fieldMatcher + ") checkers: " + checkerNamesStr;
//            MultiCheckResult<CheckResult<?>> multiCheckResult = multiCheckerExecutor.toMultiCheckResult(log);
//            multiCheckResult.setCheckLogicFrom("combinatorial by config,see log");
//            return multiCheckResult;
//        }
    }


}


