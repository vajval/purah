package io.github.vajval.purah.core.checker.custom;


import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.*;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.resolver.ArgResolver;
import io.github.vajval.purah.spring.aop.ann.CheckIt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class AllFieldCheckItSpelChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {

    final ExecMode.Main mainExecType;
    final ResultLevel resultLevel;
    public AllFieldCheckItSpelChecker(ExecMode.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;

    }


    protected abstract Purahs purahs();

    protected abstract String spel(String value, Map<String, ?> map,InputToCheckerArg<Object> inputToCheckerArg);

    protected abstract FieldMatcher fieldMatcher(InputToCheckerArg<Object> inputToCheckerArg);


    @Override
    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        Purahs purahs = purahs();
        ArgResolver argResolver = purahs.argResolver();
        FieldMatcher fieldMatcher = this.fieldMatcher(inputToCheckerArg);
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
        String log = inputToCheckerArg.fieldPath() + "  " + this.name();
        Map<String, ?> map = matchFieldObjectMap.entrySet().stream().filter(i -> i.getValue().argValue() != null).collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().argValue()));
        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(mainExecType, resultLevel,log);
        for (InputToCheckerArg<?> value : matchFieldObjectMap.values()) {
            CheckIt checkIt = value.annOnField(CheckIt.class);
            String[] array = (String[]) Arrays.stream(checkIt.value()).map(i -> spel(i, map,inputToCheckerArg)).toArray();
            ComboBuilderChecker combo = purahs.combo(array).mainMode(checkIt.mainMode()).resultLevel(checkIt.resultLevel());
            multiCheckerExecutor.add(combo,value);
        }

        return multiCheckerExecutor.execToMultiCheckResult();
    }


}