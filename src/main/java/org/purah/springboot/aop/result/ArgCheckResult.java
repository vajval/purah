package org.purah.springboot.aop.result;

import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.*;
import org.purah.core.exception.UnexpectedException;
import org.purah.springboot.aop.ann.CheckIt;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ArgCheckResult extends MultiCheckResult<CheckResult<?>> {

    CheckIt checkItAnn;
    Object checkArg;

    LinkedHashMap<String, CheckResult<?>> checkResultMap;
    protected ExecMode.Main execMode;


    private ArgCheckResult(BaseOfMultiCheckResult mainCheckResult, List<CheckResult<?>> valueList) {
        super(mainCheckResult, valueList);
    }

    private ArgCheckResult(BaseOfMultiCheckResult mainCheckResult, LinkedHashMap<String, CheckResult<?>> checkResultMap, List<CheckResult<?>> valueList, CheckIt checkItAnn, Object checkArg, ExecMode.Main execMode) {

        super(mainCheckResult, valueList);
        this.checkItAnn = checkItAnn;
        this.checkResultMap = checkResultMap;
        this.checkArg = checkArg;
        this.execMode = execMode;
    }

    public static ArgCheckResult noAnnIgnore() {
        ArgCheckResult argCheckResult = new ArgCheckResult(BaseOfMultiCheckResult.ignore("no ann on Parameter"), Collections.emptyList());
        argCheckResult.checkResultMap = new LinkedHashMap<>();
        return argCheckResult;
    }


    public static ArgCheckResult create(BaseOfMultiCheckResult mainCheckResult, List<String> checkNameList, List<CheckResult<?>> valueList, CheckIt checkItAnn, Object checkArg, ExecMode.Main methodExecType

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

    public CheckResult<?> resultOf(String name) {
        return checkResultMap.get(name);
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
        throw new UnexpectedException("this arg not check,no ann or be skip");

//        if (execMode == ExecMode.Main.at_least_one) {
//            throw new RuntimeException("unable to know,mode:[at_least_one],at least one has already succeeded.So this check is skipped,If the check must be conducted regardless. @MethodCheck set mainMode  at_least_one_but_must_check_all");
//        } else if (execMode == ExecMode.Main.all_success) {
//            throw new RuntimeException("unable to know,mode:[all_success],at least one has already failed.So this check is skipped,If the check must be conducted regardless. @MethodCheck set mainMode  all_success_but_must_check_all");
//        } else {
//            throw new UnexpectedException(execMode.name());
//        }
    }

    public static LogicCheckResult<?> fill(CheckIt checkIt) {
        if (checkIt.mainMode() == ExecMode.Main.at_least_one) {
            return LogicCheckResult.ignore("mode:[at_least_one] ,at least one has already succeeded.So this check is skipped");
        } else if (checkIt.mainMode() == ExecMode.Main.all_success) {
            return LogicCheckResult.ignore("mode:[all_success] ,at least one has already failed.So this check is skipped");
        } else {
            throw new UnexpectedException(checkIt.mainMode().name());

        }
    }

    @Override
    public String toString() {
        String checkNames = "";
        if (!CollectionUtils.isEmpty(checkResultMap)) {
            checkNames = ",checkNames=" + checkResultMap.keySet();
        }

        String dataStr = "";
        if (!CollectionUtils.isEmpty(valueList)) {
            dataStr = ", data=" + valueList;
        }
        return "ArgCheckResult{" +
                " base=" + base +
                checkNames +
                dataStr +
                '}';
    }
}
