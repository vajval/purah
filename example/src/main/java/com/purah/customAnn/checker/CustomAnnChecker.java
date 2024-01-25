package com.purah.customAnn.checker;


import com.purah.base.Name;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.context.SingleCheckerResult;
import com.purah.customAnn.ann.CNPhoneNum;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.customAnn.ann.NotEmpty;
import com.purah.customAnn.ann.Range;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Name("自定义注解检测")
@EnableOnPurahContext
@Component
public class CustomAnnChecker extends BaseChecker {
    Map<Class<? extends Annotation>, BiFunction<Annotation, CheckInstance, CheckerResult>> map = new HashMap<>();


    public CustomAnnChecker() {
        map.put(Range.class, (a, b) -> range((Range) a, b));
        map.put(NotEmpty.class, (a, b) -> notEmpty((NotEmpty) a, b));
        map.put(CNPhoneNum.class, (a, b) -> cnPhoneNum((CNPhoneNum) a, b));
    }

    @Override
    public CheckerResult doCheck(CheckInstance checkInstance) {

        List<Annotation> annotations = ((CheckInstance<?>) checkInstance).getAnnotations();
        for (Annotation annotation : annotations) {
            BiFunction<Annotation, CheckInstance, CheckerResult> biFunction = map.get(annotation.annotationType());
            if (biFunction == null) continue;
            CheckerResult checkerResult = biFunction.apply(annotation, checkInstance);

            if (checkerResult.isFailed()) {
                return checkerResult;
            }

        }

        return SingleCheckerResult.success();
    }


    public CheckerResult cnPhoneNum(CNPhoneNum cnPhoneNum, CheckInstance<String> str) {
        String strValue = str.instance();
        //gpt 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success("正确的");
        }
        return failed(cnPhoneNum.errorMsg());


    }

    public CheckerResult notEmpty(NotEmpty notEmpty, CheckInstance<String> str) {
        String strValue = str.instance();
        if (StringUtils.hasText(strValue)) {
            return success("正确的");
        }
        return failed(notEmpty.errorMsg());


    }


    public CheckerResult range(Range range, CheckInstance<Number> num) {

        Number numValue = num.instance();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return failed(range.errorMsg());
        }
        return success("参数合规");

    }


}
