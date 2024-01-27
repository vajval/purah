package com.purah.customAnn.checker;


import com.purah.base.Name;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.checker.custom.AbstractCustomAnnChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.customAnn.ann.CNPhoneNum;
import com.purah.customAnn.ann.NotNull;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.customAnn.ann.NotEmpty;
import com.purah.customAnn.ann.Range;
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

        //gpt 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success("正确的");
        }
        return SingleCheckerResult.failed(str.instance(), str.fieldStr() + ":" + cnPhoneNum.errorMsg());


    }

    public CheckerResult notEmpty(NotEmpty notEmpty, CheckInstance<String> str) {
        String strValue = str.instance();
        if (StringUtils.hasText(strValue)) {
            return success("正确的");
        }
        return SingleCheckerResult.failed(str.instance(), str.fieldStr() + ":" + notEmpty.errorMsg());


    }


    public CheckerResult range(Range range, CheckInstance<Number> num) {
        Number numValue = num.instance();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return SingleCheckerResult.failed(num.instance(), (num.fieldStr() + ":" + range.errorMsg()));
        }
        return success("参数合规");

    }


}
