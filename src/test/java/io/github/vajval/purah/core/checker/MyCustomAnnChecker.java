package io.github.vajval.purah.core.checker;


import io.github.vajval.purah.core.checker.ann.CNPhoneNum;
import io.github.vajval.purah.core.checker.ann.NotEmptyTest;
import io.github.vajval.purah.core.checker.ann.NotNull;
import io.github.vajval.purah.core.checker.ann.Range;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.custom.CustomAnnChecker;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.name.Name;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


import static io.github.vajval.purah.core.checker.MyCustomAnnChecker.NAME;


@Name(NAME)
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {

    public static final String NAME = "custom_ann_check";
    public static int cnPhoneNumCount = 0;

    public MyCustomAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.only_failed_only_base_logic);
    }

//    @Override
//    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
////        return LogicCheckResult.success();
//        List<Annotation> enableAnnotations = inputToCheckerArg.annListOnField().stream()
//                .filter(i -> annCheckerMapping.containsKey(i.annotationType()))
//                .collect(Collectors.toList());
//        for (Annotation enableAnnotation : enableAnnotations) {
//            if (inputToCheckerArg.isNull()) continue;
//            boolean success;
//            if (enableAnnotation.annotationType().equals(CNPhoneNum.class)) {
//                success = cnPhoneNum((CNPhoneNum) enableAnnotation, (InputToCheckerArg) inputToCheckerArg).isSuccess();
//            } else if (enableAnnotation.annotationType().equals(NotEmptyTest.class)) {
//                success = notEmpty((NotEmptyTest) enableAnnotation, (InputToCheckerArg) inputToCheckerArg).isSuccess();
//            } else if (enableAnnotation.annotationType().equals(Range.class)) {
//                success = range((Range) enableAnnotation, (InputToCheckerArg) inputToCheckerArg).isSuccess();
//            } else {
//                success = notNull((NotNull) enableAnnotation, (Integer) inputToCheckerArg.argValue());
//            }
//            if (!success) {
//               break;
//            }
//        }
//        return new MultiCheckResult<>(LogicCheckResult.success(), Collections.emptyList());
//    }

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
