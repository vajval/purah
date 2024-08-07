package io.github.vajval.purah.core.checker.combinatorial;


import io.github.vajval.purah.core.checker.*;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.ExecInfo;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.resolver.ArgResolver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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


    public CombinatorialCheckerConfig config;


    public CombinatorialChecker(CombinatorialCheckerConfig config) {
        this.config = config;
        int size = config.forRootInputArgCheckerNames.size() + config.fieldMatcherCheckerConfigList.size();
        if (config.mainExecType == ExecMode.Main.all_success) {
            for (int i = 0; i < size; i++) {
                fastFailedStopMap.put(i, i);
                fastFailedCountMap.put(i, new AtomicInteger(0));
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

        /*
           check inputArg
         */
        Purahs purahs = config.purahs;
        ArrayList<CheckerExec> suppliers = new ArrayList<>(size);
        List<Checker<?, ?>> rootInputArgCheckers = this.config.forRootInputArgCheckerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());
        for (Checker<?, ?> checker : rootInputArgCheckers) {
            suppliers.add(new CheckerExec(checker, inputToCheckerArg));
        }
        /*
         * check  inputArg matched field values
         */
        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : config.fieldMatcherCheckerConfigList) {
            FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor
                    = new FieldMatcherCheckerConfigExecutor(purahs, fieldMatcherCheckerConfig, config.mainExecType, config);
            suppliers.add(new CheckerExec(fieldMatcherCheckerConfigExecutor, inputToCheckerArg));
//            suppliers.addAll(fieldMatcherCheckerConfigExecutor.checkResultSupplierList(inputToCheckerArg));
        }


        MultiCheckerExecutor executor = new MultiCheckerExecutor(this.config.mainExecType, this.config.resultLevel);
        for (CheckerExec supplier : suppliers) {
            executor.add(supplier);
        }

        String log = "[" + inputToCheckerArg.fieldPath() + "]: " + this.name();
        MultiCheckResult<CheckResult<?>> multiCheckResult = executor.toMultiCheckResult(log);
        multiCheckResult.setCheckLogicFrom(this.logicFrom());

        return multiCheckResult;

    }



    public static class FieldMatcherCheckerConfigExecutor implements Checker<Object, List<CheckResult<?>>> {
        final FieldMatcher fieldMatcher;
        final ExecMode.Main mainMode;
        final ArgResolver argResolver;
        final NewExecutor newExecutor;
        List<Checker<?, ?>> checkerList;
        String log;
        public FieldMatcherCheckerConfigExecutor(Purahs purahs, FieldMatcherCheckerConfig fieldMatcherCheckerConfig, ExecMode.Main mainMode, CombinatorialCheckerConfig config) {
            this.fieldMatcher = fieldMatcherCheckerConfig.fieldMatcher;
            this.mainMode = mainMode;
            argResolver = config.purahs.argResolver();
            checkerList = fieldMatcherCheckerConfig.checkerNames.stream().map(purahs::checkerOf).collect(Collectors.toList());
             log = "[" + config.name + "]  of match:(" + fieldMatcherCheckerConfig.fieldMatcher + ") checkers: " + fieldMatcherCheckerConfig.checkerNames;
            newExecutor = new NewExecutor(mainMode, ResultLevel.only_failed_only_base_logic, checkerList, log);
        }

        @Override
        public MultiCheckResult<CheckResult<?>> check(InputToCheckerArg<Object> inputToCheckerArg) {


            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);

            MultiCheckerExecutor executor = new MultiCheckerExecutor(mainMode, ResultLevel.all);
            for (Checker<?, ?> checker : checkerList) {
                for (InputToCheckerArg<?> value : matchFieldObjectMap.values()) {
                    executor.add(checker, value);
                }
            }
            return executor.toMultiCheckResult(log);
        }
    }
//
Map<Integer, Integer> fastFailedStopMap = new HashMap<>();
    Map<Integer, AtomicInteger> fastFailedCountMap = new HashMap<>();
    AtomicInteger checkCount = new AtomicInteger(0);
    final int orderCount = 100;
    int size;
//
//    protected void autoReMap(List<ExecInfo> execInfoList) {
//        if (config.mainExecType != ExecMode.Main.all_success) {
//            return;
//        }
//        count(execInfoList);
//        int i = checkCount.addAndGet(1);
//        if (i % orderCount == 0) {
//            reMap();
//        }
//    }
//
//    protected void reMap() {
//        //fastFailedStopMap:{1:3,2:1,3:2}  第一步执行第三个, 第二执行第一个, 第三步执行第二个
//
//        List<Integer> sort = fastFailedCountMap.entrySet().stream().sorted(Comparator.comparing(
//                (a) -> a.getValue().get()
//        )).map(Map.Entry::getKey).collect(Collectors.toList());
//        //sort 3 1 2    当前第三步执行的放最前面 (2), 当前第一步执行的防最第二(3) 当前第二步执行的防最第三(1)
//        Map<Integer, Integer> newFastFailedStopMap = new HashMap<>();
//        for (Integer i : sort) {
//            int index = fastFailedStopMap.get(i);
//            newFastFailedStopMap.put(index, i);
//        }
//        this.fastFailedStopMap = newFastFailedStopMap;
//    }
//
//    protected void count(List<ExecInfo> execInfoList) {
//        for (int index = 0; index < execInfoList.size(); index++) {
//            ExecInfo execInfo = execInfoList.get(index);
//            if (execInfo == ExecInfo.failed) {
//                fastFailedCountMap.get(index).addAndGet(1);
//            }
//        }
//
//    }

}


