package org.purah.example.customAnn.checker;


import org.purah.core.base.Name;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.combinatorial.ExecType;
import org.purah.core.checker.method.toChecker.CheckerByAnnMethod;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.custom.AbstractCustomAnnChecker;
import org.purah.core.checker.result.ResultLevel;
import org.purah.example.customAnn.ann.CNPhoneNum;
import org.purah.example.customAnn.ann.NotEmptyTest;
import org.purah.example.customAnn.ann.NotNull;
import org.purah.example.customAnn.ann.Range;
import org.purah.springboot.ann.EnableBeanOnPurahContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;


@Name("自定义注解检测")
@EnableBeanOnPurahContext
@Component
public class CustomAnnChecker extends AbstractCustomAnnChecker {

    public static int cnPhoneNumCount = 0;

    public CustomAnnChecker() {
        super(ExecType.Main.all_success, ResultLevel.failedAndIgnoreNotBaseLogic);
    }

    @Override
    public Checker methodToChecker(Object methodsToCheckersBean, Method method, String name) {
        return new CheckerByAnnMethod(methodsToCheckersBean, method, name);
    }

    public boolean notNull(NotNull notNull, Integer age) {
        if (age == null) {
            return false;
        }
        return true;


    }

    public CheckResult cnPhoneNum(CNPhoneNum cnPhoneNum, CheckInstance<String> str) {
        String strValue = str.instance();
        cnPhoneNumCount++;

        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success(str, "正确的");
        }
        return BaseLogicCheckResult.failed(str.instance(), str.fieldStr() + ":" + cnPhoneNum.errorMsg());


    }

    public CheckResult notEmpty(NotEmptyTest notEmptyTest, CheckInstance<String> str) {
        String strValue = str.instance();
        if (StringUtils.hasText(strValue)) {
            return success(str, "正确的");
        }
        return BaseLogicCheckResult.failed(str.instance(), str.fieldStr() + ":" + notEmptyTest.errorMsg());


    }


    public CheckResult range(Range range, CheckInstance<Number> num) {
        Number numValue = num.instance();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return BaseLogicCheckResult.failed(num.instance(), (num.fieldStr() + ":" + range.errorMsg()));
        }
        return success(num, "参数合规");

    }


}
