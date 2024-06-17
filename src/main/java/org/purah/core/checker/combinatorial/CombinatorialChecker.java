package org.purah.core.checker.combinatorial;


import org.purah.core.base.IName;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.Checker;
import org.purah.core.checker.CheckerManager;
import org.purah.core.checker.base.*;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.CombinatorialCheckResult;
import org.purah.core.matcher.FieldMatcher;
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
public class CombinatorialChecker extends AbstractBaseSupportCacheChecker<Object, Object> {

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
    public CheckResult doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        if (!init) {
            init();
        }


        MultiCheckerExecutor executor = createMultiCheckerExecutor();


        /*
          对入参对象的检查
         */
        for (Checker checker : rootInstanceCheckers) {
            executor.add(inputToCheckerArg, checker);
        }
        /*
         * 对入参对象中FieldMatcher 匹配的字段进行对应的检查
         */

        for (FieldMatcherCheckerConfig fieldMatcherCheckerConfig : fieldMatcherCheckerConfigList) {
            FieldMatcherCheckerConfigExecutor fieldMatcherCheckerConfigExecutor = new FieldMatcherCheckerConfigExecutor(fieldMatcherCheckerConfig);
            executor.add(() -> fieldMatcherCheckerConfigExecutor.check(inputToCheckerArg));
        }


        String log = "[" + inputToCheckerArg.fieldStr() + "]: " + this.name();
        CombinatorialCheckResult result = executor.toCombinatorialCheckResult(log);
        result.setCheckLogicFrom(this.logicFrom());
        return result;

    }


    public ArgResolverManager getArgResolverManager() {
        return config.purahContext.argResolverManager();
    }

    private MultiCheckerExecutor createMultiCheckerExecutor() {
        return new MultiCheckerExecutor( this.config.mainExecType, this.config.resultLevel);
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


        public CheckResult check(InputToCheckerArg<Object> inputToCheckerArg) {

            ArgResolver argResolver = getArgResolverManager().getArgResolver(inputToCheckerArg.argClass());




            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcherCheckerConfig.fieldMatcher);

            ExecType.Matcher execType = fieldMatcherCheckerConfig.execType;

            MultiCheckerExecutor multiCheckerExecutor = CombinatorialChecker.this.createMultiCheckerExecutor();

            // 对每个匹配到的字段

            if (execType == ExecType.Matcher.checker_instance) {
                for (Checker checker : checkerList) {
                    for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
                        multiCheckerExecutor.add(entry.getValue(), checker);
                    }
                }
            } else if (execType == ExecType.Matcher.instance_checker) {
                for (Map.Entry<String, InputToCheckerArg<?>> entry : matchFieldObjectMap.entrySet()) {
                    for (Checker checker : checkerList) {
                        multiCheckerExecutor.add(entry.getValue(), checker);
                    }
                }
            } else {
                throw new RuntimeException();
            }


            String checkerNamesStr = fieldMatcherCheckerConfig.getCheckers().stream().map(IName::name).collect(Collectors.joining(","));
            FieldMatcher fieldMatcher = fieldMatcherCheckerConfig.fieldMatcher;

            String log = inputToCheckerArg.fieldStr() + " match:(" + fieldMatcher + ") checkers: " + checkerNamesStr;
            CombinatorialCheckResult result = multiCheckerExecutor.toCombinatorialCheckResult(log);

            result.setCheckLogicFrom("combinatorial by config,see log");
            return result;
        }
    }


}


