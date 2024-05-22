package org.purah.example.customAnn.checker;


import org.purah.core.base.Name;
import org.purah.core.checker.CheckInstance;
import org.purah.core.checker.result.CheckerResult;
import org.purah.core.checker.result.SingleCheckerResult;
import org.purah.core.checker.custom.AbstractCustomAnnChecker;
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

    public boolean notNull(NotNull notNull, Integer age) {
        if (age == null) {
            return false;
        }
        return true;


    }

    public CheckerResult cnPhoneNum(CNPhoneNum cnPhoneNum, CheckInstance<String> str) {
        String strValue = str.instance();

        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success(str,"正确的");
        }
        return SingleCheckerResult.failed(str.instance(), str.fieldStr() + ":" + cnPhoneNum.errorMsg());


    }

    public CheckerResult notEmpty(NotEmpty notEmpty, CheckInstance<String> str) {
        String strValue = str.instance();
        if (StringUtils.hasText(strValue)) {
            return success(str,"正确的");
        }
        return SingleCheckerResult.failed(str.instance(), str.fieldStr() + ":" + notEmpty.errorMsg());


    }


    public CheckerResult range(Range range, CheckInstance<Number> num) {
        Number numValue = num.instance();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return SingleCheckerResult.failed(num.instance(), (num.fieldStr() + ":" + range.errorMsg()));
        }
        return success(num,"参数合规");

    }


}
