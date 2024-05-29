package org.purah.springboot.result;

import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.*;
import org.purah.springboot.ann.CheckIt;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ArgCheckResult extends MultiCheckResult<CheckResult<?>> {


    CheckIt checkItAnn;
    Object checkArg;

    LinkedHashMap<String, CheckResult<?>> checkResultMap;
    protected ExecType.Main methodExecType;


    private ArgCheckResult(BaseLogicCheckResult mainCheckResult, LinkedHashMap<String, CheckResult<?>> checkResultMap,
                           List<CheckResult<?>> valueList, CheckIt checkItAnn, Object checkArg, ExecType.Main methodExecType
    ) {

        super(mainCheckResult, valueList);
        this.checkItAnn = checkItAnn;
        this.checkResultMap = checkResultMap;
        this.checkArg = checkArg;
        this.methodExecType = methodExecType;
    }

    public LinkedHashMap<String, CheckResult<?>> checkResultMap() {
        return checkResultMap;
    }

    public static ArgCheckResult create(BaseLogicCheckResult mainCheckResult,
                                        List<String> checkNameList,
                                        List<CheckResult<?>> valueList,
                                        CheckIt checkItAnn, Object checkArg, ExecType.Main methodExecType

    ) {

        Iterator<String> iterator = checkNameList.iterator();


        LinkedHashMap<String, CheckResult<?>> checkResultMap = new LinkedHashMap<>();
        for (CheckResult<?> checkResult : valueList) {
            checkResultMap.put(iterator.next(), checkResult);

        }

        while (iterator.hasNext()) {
            checkResultMap.put(iterator.next(), fill(checkItAnn));
        }
        return new ArgCheckResult(mainCheckResult, checkResultMap, valueList, checkItAnn, checkArg, methodExecType);

    }


    @Override
    public boolean isSuccess() {
        if (isIgnore()) {
            ignoreError();
        }
        return super.isSuccess();
    }

    @Override
    public boolean isFailed() {
        if (isIgnore()) {
            ignoreError();
        }
        return super.isFailed();
    }

    @Override
    public boolean isError() {
        if (isIgnore()) {
            ignoreError();
        }
        return super.isError();
    }


    public void ignoreError() {
        if (methodExecType == ExecType.Main.at_least_one) {
            throw new RuntimeException("无法检测 at_least_one 已经有一个成功了，所以此参数没有检测被忽视，若无论如何也要检测，请@MethodCheckExecType设置为 at_least_one_but_must_check_all");
        } else if (methodExecType == ExecType.Main.all_success) {
            throw new RuntimeException("无法检测 all_success 已经有一个失败了，所以此参数没有检测被忽视，若无论如何也要检测，请@MethodCheckExecType设置为 all_success_but_must_check_all");
        } else {
            throw new RuntimeException("不该出错");
        }
    }

    public static BaseLogicCheckResult fill(CheckIt checkIt) {
        if (checkIt.execType() == ExecType.Main.at_least_one) {
            return BaseLogicCheckResult.ignore(" at_least_one 已经有一个成功了，所以没有检测 被忽视 ");
        } else if (checkIt.execType() == ExecType.Main.all_success) {
            return BaseLogicCheckResult.ignore("all_success 已经有一个失败了，所以没有检测  被忽视");
        } else {
            throw new RuntimeException();
        }
    }

    public CheckResult resultOf(String name) {
        return checkResultMap.get(name);

    }


}
