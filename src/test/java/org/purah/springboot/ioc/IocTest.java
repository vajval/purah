package org.purah.springboot.ioc;

import com.google.common.collect.Lists;
import org.purah.core.base.Name;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.example.customAnn.ann.NotEmptyTest;
import org.purah.springboot.ann.EnableBeanOnPurahContext;
import org.purah.springboot.ann.PurahEnableMethods;
import org.purah.springboot.ann.ToChecker;
import org.purah.springboot.ann.ToCheckerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

@PurahEnableMethods//将这个对象的函数转化为核对器
@EnableBeanOnPurahContext//使之生效
public class IocTest {


    public static List<String> checkerMethodNameList = Lists.newArrayList(
            "notEmptyIntegerIocTest", "notEmptyStringIocTest", "hasTestIocTest","notEmptyAnnIocTest");

    @ToChecker(name = "notEmptyIntegerIocTest")
    public boolean IocTestNotNull(Integer o) {
        return o != null;
    }

    @ToChecker
    @Name("notEmptyStringIocTest")
    public boolean IocTestNotNull(InputCheckArg<String> o) {
        return o != null;
    }

    @ToChecker(name = "hasTestIocTest")
    public CheckResult hasTest(InputCheckArg<String> inputCheckArg) {
        if (StringUtils.hasText(inputCheckArg.inputArg())) {
            return BaseLogicCheckResult.success(null, "有文本");
        } else {
            return BaseLogicCheckResult.failed(null, "无文本");
        }
    }

    @ToChecker(name = "notEmptyAnnIocTest")
    public CheckResult hasTest(NotEmptyTest notEmptyTest, InputCheckArg<String> inputCheckArg) {
        if (StringUtils.hasText(inputCheckArg.inputArg())) {
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
