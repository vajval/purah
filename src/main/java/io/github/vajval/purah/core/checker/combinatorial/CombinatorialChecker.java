package io.github.vajval.purah.core.checker.combinatorial;


import io.github.vajval.purah.core.checker.*;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.resolver.ArgResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
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
    private static final Logger logger = LogManager.getLogger(CombinatorialChecker.class);

    CombinatorialCheckerConfig config;
    int size;
    Purahs purahs;
    List<Checker<?, ?>> rootInputArgCheckers;
    List<FieldMatcherCheckerConfigExecutor> fieldMatcherCheckerConfigExecutors;
    ReOrder reOrder;

    public CombinatorialChecker(CombinatorialCheckerConfig config) {
        this.config = config;
        size = config.forRootInputArgCheckerNames.size() + config.fieldMatcherCheckerConfigList.size();
        purahs = config.purahs;
        rootInputArgCheckers = this.config.forRootInputArgCheckerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());
        fieldMatcherCheckerConfigExecutors = this.config.fieldMatcherCheckerConfigList.stream().map(i -> new FieldMatcherCheckerConfigExecutor(i, purahs, config)).collect(Collectors.toList());
        if (config.mainExecType == ExecMode.Main.all_success || config.mainExecType == ExecMode.Main.at_least_one) {
            int reOrderCount = config.getReOrderCount();
            if (reOrderCount != -1) {
                if (reOrderCount < size) {
                    logger.warn("checker {} reOrderCount{} less than size{}  re order not enable", config.name, reOrderCount, size);
                } else {
                    reOrder = new ReOrder(config.mainExecType, size, config.getReOrderCount(), config.name);
                }
            }
        }

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
    public MultiCheckResult<CheckResult<?>> oCheck(Object o) {
        return (MultiCheckResult) super.oCheck(o);
    }

    @Override
    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {

        //check inputArg
        List<CheckerExec> suppliers = new ArrayList<>(size);
        for (Checker<?, ?> checker : rootInputArgCheckers) {
            suppliers.add(new CheckerExec(checker, inputToCheckerArg));
        }


        //check  inputArg matched field values
        for (FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor : fieldMatcherCheckerConfigExecutors) {
            suppliers.add(new CheckerExec(fieldMatcherCheckerConfigExecutor, inputToCheckerArg));
//            suppliers.addAll(fieldMatcherCheckerConfigExecutor.checkResultSupplierList(inputToCheckerArg));
        }


        String log = "[" + inputToCheckerArg.fieldPath() + "]: " + this.name();
        MultiCheckerExecutor executor = new MultiCheckerExecutor(this.config.mainExecType, this.config.resultLevel, log);
        if (reOrder != null) {
            suppliers = reOrder.reOrder(suppliers);
        }
        for (CheckerExec supplier : suppliers) {
            executor.add(supplier);
        }
        MultiCheckResult<CheckResult<?>> multiCheckResult = executor.execToMultiCheckResult();
        if (reOrder != null) {
            reOrder.count(executor.getExecInfoList());
        }
        multiCheckResult.setCheckLogicFrom(this.logicFrom());
        return multiCheckResult;
    }


    public static class FieldMatcherCheckerConfigExecutor implements Checker<Object, List<CheckResult<?>>> {
        final FieldMatcher fieldMatcher;

        final ArgResolver argResolver;

        List<Checker<?, ?>> checkerList;
        String log;
        ExecMode.Main mainMode;

        public FieldMatcherCheckerConfigExecutor(FieldMatcherCheckerConfig config, Purahs purahs, CombinatorialCheckerConfig combinatorialCheckerConfig) {
            this.fieldMatcher = config.fieldMatcher;
            argResolver = purahs.argResolver();
            checkerList = config.checkerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());
            log = "[" + combinatorialCheckerConfig.name + "]  of match:(" + config.fieldMatcher + ") checkers: " + config.checkerNames;
            this.mainMode = combinatorialCheckerConfig.mainExecType;
        }

        @Override
        public MultiCheckResult<CheckResult<?>> check(InputToCheckerArg<Object> inputToCheckerArg) {
            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
            MultiCheckerExecutor executor = new MultiCheckerExecutor(mainMode, ResultLevel.all, log);
            for (Checker<?, ?> checker : checkerList) {
                for (InputToCheckerArg<?> value : matchFieldObjectMap.values()) {
                    executor.add(checker, value);
                }
            }
            return executor.execToMultiCheckResult();
        }
    }
}


//



