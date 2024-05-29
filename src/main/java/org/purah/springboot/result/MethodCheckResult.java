package org.purah.springboot.result;

import org.purah.core.checker.result.*;

import java.lang.reflect.Method;
import java.util.List;

public class MethodCheckResult extends MultiCheckResult<ArgCheckResult> {
    Object belongBean;
    Method method;

    public MethodCheckResult(MainOfMultiCheckResult mainCheckResult, List<ArgCheckResult> valueList, Object belongBean, Method method) {
        super(mainCheckResult, valueList);
        this.belongBean = belongBean;
        this.method = method;
    }

    public ArgCheckResult argResultOf(int index) {
        return valueList.get(index);
    }





}
