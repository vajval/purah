package org.purah.core.checker.combinatorial;


import org.purah.core.base.IName;
import org.purah.core.checker.BaseChecker;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.result.CheckerResult;
import org.purah.core.checker.result.CombinatorialCheckerResult;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.resolver.ArgResolver;
import org.purah.core.resolver.ArgResolverManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
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
        this.rootInstanceCheckers = this.config.extendCheckerNames.stream().map(checkerManager::get).collect(Collectors.toList());
        this.fieldMatcherCheckerConfigList = config.fieldMatcherCheckerConfigList;
        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : this.fieldMatcherCheckerConfigList) {
            fieldMatcherCheckerConfig.buildCheckers(checkerManager);
        }
        this.init = true;
        return this;
    }

    @Override
    public CheckerResult doCheck(CheckInstance<Object> checkInstance2) {
        if (!init) {
            init();
        }
        CheckInstance newExecTypeCheckInstance = CheckInstance.copyAndNewExecType(checkInstance2, config.mainExecType);


        MultiCheckerExecutor executor = createMultiCheckerExecutor();
        List<Supplier<CheckerResult>> supplierList = new ArrayList<>(rootInstanceCheckers.size() + fieldMatcherCheckerConfigList.size());


        /*
          对入参对象的检查
         */
        for (Checker checker : rootInstanceCheckers) {
            Supplier<CheckerResult> singleCheckerResultSupplier = () -> checker.check(newExecTypeCheckInstance);
            supplierList.add(singleCheckerResultSupplier);
        }
        /*
         * 对入参对象中FieldMatcher 匹配的字段进行对应的检查
         */

        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : fieldMatcherCheckerConfigList) {
            FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor = new FieldMatcherCheckerConfigExecutor(fieldMatcherCheckerConfig);
            supplierList.add(() -> fieldMatcherCheckerConfigExecutor.check(newExecTypeCheckInstance));
        }
        executor.exec(supplierList);

        String log = "[" + checkInstance2.fieldStr() + "]: " + this.name();
        CombinatorialCheckerResult result = executor.result(log);
        result.setCheckLogicFrom(this.logicFrom());
        return result;

    }


    public ArgResolverManager getArgResolverManager() {
        return config.purahContext.argResolverManager();
    }

    private MultiCheckerExecutor createMultiCheckerExecutor() {
        return new MultiCheckerExecutor(this.config.mainExecType, this.config.resultLevel);
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


            String checkerNamesStr = fieldMatcherCheckerConfig.getCheckers().stream().map(IName::name).collect(Collectors.joining(","));
            FieldMatcher fieldMatcher = fieldMatcherCheckerConfig.fieldMatcher;
            MultiCheckerExecutor multiCheckerExecutor = CombinatorialChecker.this.createMultiCheckerExecutor();
            multiCheckerExecutor.exec(supplierList);

            String info = checkInstance.fieldStr() + " match:(" + fieldMatcher + ") checkers: " + checkerNamesStr;
            CombinatorialCheckerResult result = multiCheckerExecutor.result(info);

            result.setCheckLogicFrom("combinatorial by config,see info");
            return result;
        }
    }


}


