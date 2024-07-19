package org.purah.util.custom;


import org.purah.core.name.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.Checker;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.converter.checker.ByAnnMethodChecker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.LogicCheckResult;
import org.purah.core.checker.AbstractCustomAnnChecker;
import org.purah.core.checker.result.ResultLevel;
import org.purah.example.customAnn.ann.CNPhoneNum;
import org.purah.example.customAnn.ann.NotEmptyTest;
import org.purah.example.customAnn.ann.NotNull;
import org.purah.example.customAnn.ann.Range;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;


@Name("custom_ann_check")
@Component
public class CustomAnnChecker extends AbstractCustomAnnChecker {

    public static int cnPhoneNumCount = 0;

    public CustomAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.failedAndIgnoreNotBaseLogic);
    }

    @Override
    public Checker methodToChecker(Object methodsToCheckersBean, Method method, String name) {
        return new ByAnnMethodChecker(methodsToCheckersBean, method, name);
    }

    public boolean notNull(NotNull notNull, Integer age) {
        if (age == null) {
            return false;
        }
        return true;


    }

    public CheckResult cnPhoneNum(CNPhoneNum cnPhoneNum, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        cnPhoneNumCount++;

        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success(str, "正确的");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldStr() + ":" + cnPhoneNum.errorMsg());


    }

    public CheckResult notEmpty(NotEmptyTest notEmptyTest, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        if (StringUtils.hasText(strValue)) {
            return success(str, "正确的");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldStr() + ":" + notEmptyTest.errorMsg());


    }


    public CheckResult range(Range range, InputToCheckerArg<Number> num) {
        Number numValue = num.argValue();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return LogicCheckResult.failed(num.argValue(), (num.fieldStr() + ":" + range.errorMsg()));
        }
        return success(num, "参数合规");

    }


}
