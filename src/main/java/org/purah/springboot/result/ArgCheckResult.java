package org.purah.springboot.result;

import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.*;
import org.purah.springboot.ann.CheckIt;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ArgCheckResult extends MultiCheckResult<CheckerResult> {


    CheckIt checkItAnn;
    Object checkArg;

    LinkedHashMap<String, CheckerResult> checkResultMap;



    private ArgCheckResult(SingleCheckerResult mainCheckResult, LinkedHashMap<String, CheckerResult> checkResultMap,
                           List<CheckerResult> valueList, CheckIt checkItAnn, Object checkArg) {

        super(mainCheckResult, valueList);
        this.checkItAnn = checkItAnn;
        this.checkResultMap = checkResultMap;
        this.checkArg = checkArg;
    }

    public LinkedHashMap<String, CheckerResult> checkResultMap() {
        return checkResultMap;
    }

    public static ArgCheckResult create(SingleCheckerResult mainCheckResult,
                                        List<String> checkNameList,
                                        List<CheckerResult> valueList,
                                        CheckIt checkItAnn, Object checkArg

    ) {

        Iterator<String> iterator = checkNameList.iterator();


        LinkedHashMap<String, CheckerResult> checkResultMap = new LinkedHashMap<>();
        for (CheckerResult checkerResult : valueList) {
            checkResultMap.put(iterator.next(), checkerResult);

        }

        while (iterator.hasNext()) {
            checkResultMap.put(iterator.next(), fill(checkItAnn));
        }

        return new ArgCheckResult(mainCheckResult, checkResultMap, valueList, checkItAnn, checkArg);

    }




    public static SingleCheckerResult fill(CheckIt checkIt) {
        if (checkIt.execType() == ExecType.Main.at_least_one) {
            return SingleCheckerResult.ignore( " at_least_one 已经有一个成功了，所以没有检测 被忽视 ");
        } else if (checkIt.execType() == ExecType.Main.all_success) {
            return SingleCheckerResult.ignore("all_success 已经有一个失败了，所以没有检测  被忽视");
        } else {
            throw new RuntimeException();
        }
    }

    public CheckerResult resultOf(String name) {
        return checkResultMap.get(name);

    }


}
