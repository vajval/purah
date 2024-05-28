package org.purah.example.customAnn.checker;


import org.purah.core.base.Name;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.custom.AbstractCustomAnnChecker;
import org.purah.core.checker.result.ResultLevel;
import org.purah.example.customAnn.ann.CNPhoneNum;
import org.purah.example.customAnn.ann.NotEmpty;
import org.purah.example.customAnn.ann.NotNull;
import org.purah.example.customAnn.ann.Range;
import org.purah.springboot.ann.EnableOnPurahContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Name("自定义注解检测")
@EnableOnPurahContext
@Component
public class CustomAnnChecker extends AbstractCustomAnnChecker {
    public CustomAnnChecker() {
        super(ExecType.Main.all_success, ResultLevel.failedIgnoreMatchByCombinatorial);
    }

    public boolean notNull(NotNull notNull, Integer age) {
        if (age == null) {
            return false;
        }
        return true;


    }

    public CheckResult cnPhoneNum(CNPhoneNum cnPhoneNum, CheckInstance<String> str) {
        String strValue = str.instance();

        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success(str,"正确的");
        }
        return BaseLogicCheckResult.failed(str.instance(), str.fieldStr() + ":" + cnPhoneNum.errorMsg());


    }

    public CheckResult notEmpty(NotEmpty notEmpty, CheckInstance<String> str) {
        String strValue = str.instance();
        if (StringUtils.hasText(strValue)) {
            return success(str,"正确的");
        }
        return BaseLogicCheckResult.failed(str.instance(), str.fieldStr() + ":" + notEmpty.errorMsg());


    }


    public CheckResult range(Range range, CheckInstance<Number> num) {
        Number numValue = num.instance();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return BaseLogicCheckResult.failed(num.instance(), (num.fieldStr() + ":" + range.errorMsg()));
        }
        return success(num,"参数合规");

    }


}
