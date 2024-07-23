package org.purah.springboot.aop.result;

import org.purah.core.checker.result.*;

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

    public ArgCheckResult argResultOf(int index) {
        return valueList.get(index);
    }

    public List<LogicCheckResult<?>> logicResultList(ResultLevel resultLevel) {

        return resultChildList(resultLevel);
    }

    public List<LogicCheckResult<?>> logicResultList() {
        return logicResultList(ResultLevel.only_failed_only_base_logic);
    }


}
