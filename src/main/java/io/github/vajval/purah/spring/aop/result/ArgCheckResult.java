package io.github.vajval.purah.spring.aop.result;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.exception.UnexpectedException;
import io.github.vajval.purah.spring.aop.ann.CheckIt;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/*

 *  @FillToMethodResult
    public MethodCheckResult checkThreeUser(
    @CheckIt("all_field_custom_ann_check") User user0,                  //argCheckResult =methodCheckResult.argResultOf(0)
    User user1,                                                         //argCheckResult =methodCheckResult.argResultOf(1)
    @CheckIt("all_field_custom_ann_check") User user2) {                //argCheckResult =methodCheckResult.argResultOf(2)
       return null;
    }

 */


public class ArgCheckResult extends MultiCheckResult<CheckResult<?>> {

    CheckIt checkItAnn;
    Object checkArg;
    LinkedHashMap<String, CheckResult<?>> checkResultMap;
    protected ExecMode.Main execMode;


    private ArgCheckResult(LogicCheckResult<?> mainCheckResult, List<CheckResult<?>> valueList) {
        super(mainCheckResult, valueList);
    }

    private ArgCheckResult(LogicCheckResult<?> mainCheckResult, LinkedHashMap<String, CheckResult<?>> checkResultMap, List<CheckResult<?>> valueList, CheckIt checkItAnn, Object checkArg, ExecMode.Main execMode) {

        super(mainCheckResult, valueList);
        this.checkItAnn = checkItAnn;
        this.checkResultMap = checkResultMap;
        this.checkArg = checkArg;
        this.execMode = execMode;
    }

    public static ArgCheckResult noAnnIgnore() {
        ArgCheckResult argCheckResult = new ArgCheckResult(LogicCheckResult.ignore("ignore because no ann on Parameter"), Collections.emptyList());
        argCheckResult.checkResultMap = new LinkedHashMap<>();
        return argCheckResult;
    }


    public static ArgCheckResult create(LogicCheckResult<?> mainCheckResult, List<String> checkNameList, List<CheckResult<?>> valueList, CheckIt checkItAnn, Object checkArg, ExecMode.Main methodExecType
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


    private void ignoreError() {
        throw new UnexpectedException("this arg not check,no ann or be skip");
    }

    public List<LogicCheckResult<?>> failedLogicList() {
        return resultChildList(ResultLevel.only_failed_only_base_logic);
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
                " base=" + mainResult +
                checkNames +
                dataStr +
                '}';
    }
}
