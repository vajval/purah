package org.purah.util;

import org.purah.core.base.Name;
import org.purah.core.checker.converter.checker.FVal;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.springboot.ann.PurahMethodsRegBean;
import org.purah.springboot.ann.convert.ToChecker;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@PurahMethodsRegBean

public class ExampleBean {

    @Name("用户检测")
    @ToChecker
    public CheckResult<String> test(
            @FVal("#root#") Object people,
            @FVal("address") String address,
            @FVal("child") List<People> childNameList,
            @FVal("child#0.name") String child0Name,
            @FVal("child#0.name") TestAnn testAnnOnNameField) {
        if (people == null) {
            return BaseLogicCheckResult.failed("入参不能为空");
        }
        if (!StringUtils.hasText(address)) {
            return BaseLogicCheckResult.failed("地址不能为空");

        }
        if (CollectionUtils.isEmpty(childNameList)) {
            return BaseLogicCheckResult.failed("至少要有一个孩子");

        }
        if (!Objects.equals(child0Name,"儿子")) {
            return BaseLogicCheckResult.failed("大儿子的名字必须是儿子");
        }
        if (testAnnOnNameField == null || !StringUtils.hasText(testAnnOnNameField.value())) {
            return BaseLogicCheckResult.failed("注解必须有值");

        }
        return BaseLogicCheckResult.success();


    }

    @Name("年龄检测")
    public boolean test(@FVal("age") Integer age) {
        return age >= 18;
    }

}
