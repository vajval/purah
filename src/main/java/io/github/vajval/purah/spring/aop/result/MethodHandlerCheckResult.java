package io.github.vajval.purah.spring.aop.result;

import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;

import java.lang.reflect.Method;
import java.util.List;


public class MethodHandlerCheckResult extends MultiCheckResult<ArgCheckResult> {
    final Object belongBean;
    final Method method;

    public MethodHandlerCheckResult(LogicCheckResult<?> mainCheckResult, List<ArgCheckResult> valueList, Object belongBean, Method method) {
        super(mainCheckResult, valueList);
        this.belongBean = belongBean;
        this.method = method;
    }

    public List<ArgCheckResult> argCheckResultList() {
        return valueList;
    }

    public ArgCheckResult argResultOf(int index) {
        return valueList.get(index);
    }

    public List<LogicCheckResult<?>> childList(ResultLevel resultLevel) {

        return resultChildList(resultLevel);
    }

    public List<LogicCheckResult<?>> failedLogicList() {
        return childList(ResultLevel.only_failed_only_base_logic);
    }


}
