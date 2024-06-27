package org.purah.springboot.ioc;

import com.google.common.collect.Lists;
import org.purah.core.base.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.example.customAnn.ann.NotEmptyTest;
import org.purah.springboot.ann.PurahMethodsRegBean;
import org.purah.springboot.ann.convert.ToChecker;
import org.purah.springboot.ann.convert.ToCheckerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

@PurahMethodsRegBean//将这个对象的函数转化为核对器
public class IocTest {


    public static List<String> checkerMethodNameList = Lists.newArrayList(
            "notEmptyIntegerIocTest", "notEmptyStringIocTest", "hasTestIocTest","notEmptyAnnIocTest");

    @ToChecker
    @Name("notEmptyIntegerIocTest")
    public boolean IocTestNotNull(Integer o) {
        return o != null;
    }

    @ToChecker
    @Name("notEmptyStringIocTest")
    public boolean IocTestNotNull(InputToCheckerArg<String> o) {
        return o != null;
    }

    @ToChecker
    @Name("hasTestIocTest")
    public CheckResult hasTest(InputToCheckerArg<String> inputToCheckerArg) {
        if (StringUtils.hasText(inputToCheckerArg.argValue())) {
            return BaseLogicCheckResult.success(null, "有文本");
        } else {
            return BaseLogicCheckResult.failed(null, "无文本");
        }
    }

    @ToChecker
    @Name("notEmptyAnnIocTest")
    public CheckResult hasTest(NotEmptyTest notEmptyTest, InputToCheckerArg<String> inputToCheckerArg) {
        if (StringUtils.hasText(inputToCheckerArg.argValue())) {
            return BaseLogicCheckResult.success(null, "not empty");
        } else {
            return BaseLogicCheckResult.failed(null, "empty");
        }
    }

    @ToCheckerFactory(match = "取值必须在[*-*]之间判断IocTest")
    public boolean range(String name, Number value) {

        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }

}
