package org.purah.springboot.result;

import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.*;
import org.purah.springboot.ann.CheckIt;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ArgCheckResult extends MultiCheckResult<CheckResult> {


    CheckIt checkItAnn;
    Object checkArg;

    LinkedHashMap<String, CheckResult> checkResultMap;



    private ArgCheckResult(SingleCheckResult mainCheckResult, LinkedHashMap<String, CheckResult> checkResultMap,
                           List<CheckResult> valueList, CheckIt checkItAnn, Object checkArg) {

        super(mainCheckResult, valueList);
        this.checkItAnn = checkItAnn;
        this.checkResultMap = checkResultMap;
        this.checkArg = checkArg;
    }

    public LinkedHashMap<String, CheckResult> checkResultMap() {
        return checkResultMap;
    }

    public static ArgCheckResult create(SingleCheckResult mainCheckResult,
                                        List<String> checkNameList,
                                        List<CheckResult> valueList,
                                        CheckIt checkItAnn, Object checkArg

    ) {

        Iterator<String> iterator = checkNameList.iterator();


        LinkedHashMap<String, CheckResult> checkResultMap = new LinkedHashMap<>();
        for (CheckResult checkResult : valueList) {
            checkResultMap.put(iterator.next(), checkResult);

        }

        while (iterator.hasNext()) {
            checkResultMap.put(iterator.next(), fill(checkItAnn));
        }

        return new ArgCheckResult(mainCheckResult, checkResultMap, valueList, checkItAnn, checkArg);

    }




    public static SingleCheckResult fill(CheckIt checkIt) {
        if (checkIt.execType() == ExecType.Main.at_least_one) {
            return SingleCheckResult.ignore( " at_least_one 已经有一个成功了，所以没有检测 被忽视 ");
        } else if (checkIt.execType() == ExecType.Main.all_success) {
            return SingleCheckResult.ignore("all_success 已经有一个失败了，所以没有检测  被忽视");
        } else {
            throw new RuntimeException();
        }
    }

    public CheckResult resultOf(String name) {
        return checkResultMap.get(name);

    }


}
