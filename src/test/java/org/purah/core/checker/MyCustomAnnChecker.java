package org.purah.core.checker;


import org.purah.core.name.Name;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.LogicCheckResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.example.customAnn.ann.CNPhoneNum;
import org.purah.example.customAnn.ann.NotEmptyTest;
import org.purah.example.customAnn.ann.NotNull;
import org.purah.example.customAnn.ann.Range;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static org.purah.core.checker.MyCustomAnnChecker.NAME;


@Name(NAME)
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {

    public static final String NAME="custom_ann_check";
    public static int cnPhoneNumCount = 0;

    public MyCustomAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.only_failed_only_base_logic);
    }




    public boolean notNull(NotNull notNull, Integer age) {
        return age != null;


    }

    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        cnPhoneNumCount++;

        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {


            return LogicCheckResult.successBuildLog(str, "正确的");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + cnPhoneNum.errorMsg());


    }


    public CheckResult<?> notEmpty(NotEmptyTest notEmptyTest, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        if (StringUtils.hasText(strValue)) {
            return LogicCheckResult.successBuildLog(str, "正确的");


        }
        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + notEmptyTest.errorMsg());


    }


    public CheckResult<?> range(Range range, InputToCheckerArg<Number> num) {
        Number numValue = num.argValue();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return LogicCheckResult.failed(num.argValue(), (num.fieldPath() + ":" + range.errorMsg()));
        }
        return LogicCheckResult.successBuildLog(num, "参数合规");


    }


}
