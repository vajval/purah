package com.purah.checker.combinatorial;

import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.CheckerManager;
import com.purah.checker.context.*;
import com.purah.resolver.ArgResolver;
import com.purah.resolver.ArgResolverManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 配置的多个规则像结合形成的规则
 * easy-rule:
 * rules:
 * - name: 贷款申请
 * mapping:
 * product_city_rate_wild_card:
 * "[{直辖市}_rate}]": 直辖市利率标准检测
 * "[{一线城市}_rate}]": 一线城市市利率标准检测
 * "[{北方城市}_rate}]": 北方城市市利率标准检测
 * <p>
 * wild_card:
 * "[num_*]" : 取值范围检测
 * type_by_ann:
 * "[短文本]" : 敏感词检查
 * "[长文本]" : 敏感词检查
 */
public class CombinatorialChecker extends BaseChecker<Object, Object> {

    CombinatorialCheckerConfig config;


    /**
     * 对入参实例使用的规则检查
     */
    protected List<Checker> rootInstanceCheckers;
    /**
     * 对每个字段使用的规则检查
     */
    public List<FieldMatcherCheckerConfig> fieldMatcherCheckerConfigList = new ArrayList<>();
    private boolean init = false;

    public CombinatorialChecker(CombinatorialCheckerConfig config) {
        this.config = config;
    }

    @Override
    public String name() {
        return config.name;
    }

    public CombinatorialChecker init() {
        if (this.init) return this;
        CheckerManager checkerManager = config.purahContext.checkManager();
        this.rootInstanceCheckers = this.config.extendCheckerNames.stream().map(checkerManager::get).collect(Collectors.toList());
        this.fieldMatcherCheckerConfigList = config.fieldMatcherCheckerConfigList;
        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : this.fieldMatcherCheckerConfigList) {
            fieldMatcherCheckerConfig.buildCheckers(checkerManager);
        }
        this.init = true;
        return this;
    }

    @Override
    public CheckerResult doCheck(CheckInstance<Object> checkInstance) {
        if (!init) {
            init();
        }
        MultiCheckerExecutor executor = new MultiCheckerExecutor();
        List<Supplier<CheckerResult>> supplierList = new ArrayList<>(rootInstanceCheckers.size() + fieldMatcherCheckerConfigList.size());


        /*
          对入参对象的检查
         */
        for (Checker checker : rootInstanceCheckers) {
            Supplier<CheckerResult> singleCheckerResultSupplier = () -> checker.check(checkInstance);
            supplierList.add(singleCheckerResultSupplier);
        }
        /*
         * 对入参对象中FieldMatcher 匹配的字段进行对应的检查
         */

        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : fieldMatcherCheckerConfigList) {
            FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor = new FieldMatcherCheckerConfigExecutor(fieldMatcherCheckerConfig);
            supplierList.add(() -> fieldMatcherCheckerConfigExecutor.check(checkInstance));
        }
        executor.exec(supplierList);
        return executor.result();


    }

    /**
     * 多个checker的执行器
     */

    class MultiCheckerExecutor {


        CombinatorialCheckerResult combinatorialCheckerResult;
        ExecType.Main mainExecType;
        ExecInfo execInfo = ExecInfo.success;


        public MultiCheckerExecutor() {
            this.mainExecType = CombinatorialChecker.this.config.mainExecType;
            this.combinatorialCheckerResult = new CombinatorialCheckerResult();
        }

        public boolean exec(List<Supplier<CheckerResult>> ruleResultSupplierList) {
            boolean result = true;

            for (Supplier<CheckerResult> supplier : ruleResultSupplierList) {
                CheckerResult ruleResult = supplier.get();
                this.combinatorialCheckerResult.addResult(ruleResult);

                /*
                   有错误直接返回
                 */
                if (ruleResult.isError()) {
                    execInfo = ExecInfo.error;
                    return false;
                }
                if (ruleResult.isFailed()) {
                    if (mainExecType == ExecType.Main.all_success) {
                        // 有错误 而要求必须要全部成功，才算成功
                        execInfo = ExecInfo.failed;
                        return false;
                    } else if (mainExecType == ExecType.Main.all_success_but_must_check_all) {
                        // 有错误 而要求必须要全部成功，但是必须检查完
                        execInfo = ExecInfo.failed;
                        result = false;
                    }
                } else {
                    if (mainExecType == ExecType.Main.at_least_one) {
                        // 没有错误 而且只要一个成功就够了
                        execInfo = ExecInfo.success;
                        return true;
                    } else if (mainExecType == ExecType.Main.at_least_one_but_must_check_all) {
                        // 没有错误  但是必须检查完
                        execInfo = ExecInfo.success;
                        result = true;
                    }
                }
            }
            return result;


        }

        public CombinatorialCheckerResult result() {
            combinatorialCheckerResult.setExecInfo(execInfo);
            return combinatorialCheckerResult;
        }
    }


    public ArgResolverManager getArgResolverManager() {
        return config.purahContext.argResolverManager();
    }

    public CheckerManager getCheckerManager() {

        return config.purahContext.checkManager();
    }


    /**
     * 对一个 fieldMatcher匹配到的所有字段，进行检查
     * 检查顺序有两种
     * 1 对每个字段依次使用所有规则检查，然后下一个对象
     * 2 使用一个规则依次检查所有字段，然后下一个规则
     */

    class FieldMatcherCheckerConfigExecutor {
        FieldMatcherCheckerConfig fieldMatcherCheckerConfig;
        List<Checker> checkerList;


        public FieldMatcherCheckerConfigExecutor(FieldMatcherCheckerConfig fieldMatcherCheckerConfig) {
            checkerList = fieldMatcherCheckerConfig.getCheckers();
            this.fieldMatcherCheckerConfig = fieldMatcherCheckerConfig;
        }


        public CheckerResult check(CheckInstance<Object> checkInstance) {

            Object instance = checkInstance.instance();

            ArgResolver argResolver = getArgResolverManager().getArgResolver(instance.getClass());
            Map<String, CheckInstance> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(instance, fieldMatcherCheckerConfig.fieldMatcher);

            List<Supplier<CheckerResult>> supplierList = new ArrayList<>();
            ExecType.Matcher execType = fieldMatcherCheckerConfig.execType;

            // 对每个匹配到的字段

            if (execType == ExecType.Matcher.checker_instance) {
                for (Checker checker : checkerList) {
                    for (Map.Entry<String, CheckInstance> entry : matchFieldObjectMap.entrySet()) {
                        supplierList.add(() -> checker.check(entry.getValue()));
                    }
                }
            } else if (execType == ExecType.Matcher.instance_checker) {
                for (Map.Entry<String, CheckInstance> entry : matchFieldObjectMap.entrySet()) {
                    for (Checker checker : checkerList) {
                        supplierList.add(() -> checker.check(entry.getValue()));
                    }
                }
            } else {
                throw new RuntimeException();
            }

            MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor();
            multiCheckerExecutor.exec(supplierList);
            return multiCheckerExecutor.result();
        }
    }


}


