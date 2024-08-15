package io.github.vajval.purah.spring.ioc.test_bean.checker;

import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.converter.checker.FVal;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.spring.ioc.ann.PurahMethodsRegBean;
import io.github.vajval.purah.spring.ioc.ann.ToChecker;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.PurahUtils;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcher;
import io.github.vajval.purah.util.TestAnn;
import org.springframework.beans.factory.annotation.Autowired;

@PurahMethodsRegBean
public class CheckBean {
    @Autowired
    Purahs purahs;

    //定义逻辑,想要获取值的字段应当有getter函数
    //当user或者people对象 为null时,所有字段被填充为null,但是注解的获取不受影响
    @ToChecker("手机号所属地址检测")
    public CheckResult<?> phoneAddress(
            @FVal("phone") String phone,//phone value
            @FVal("phone") TestAnn TestAnn, //phone 字段上的注解,People为null,User为@TestAnn("123")
            @FVal("address") String address //address value
    ) {
        //......
        return LogicCheckResult.success(null, "手机号所属地址非常正确");
    }

    @ToChecker("中文名字检测")// 将函数转换为规则并且注册
    public CheckResult<?> nameCheck(String name) {
        //两种获取方法效果一样
        FieldMatcher reverseTest = purahs.matcherOf(PurahUtils.Match.reverse).create("reverse_test");
        ReverseStringMatcher reverseStringMatcher = new ReverseStringMatcher("reverse_test");

        //......
        return LogicCheckResult.success(null, "中文名字非常正确");
    }

    @ToChecker("用户注册检查")
    public Checker<?, ?> phoneAddress() {
        return purahs.combo("手机号所属地址检测")  //对user 进行`手机号所属地址检测`
                .match(new GeneralFieldMatcher("name"), "中文名字检测") //对名字为name字段进行匹配,并且进行 "中文名字检测"
                .mainMode(ExecMode.Main.all_success_but_must_check_all).resultLevel(ResultLevel.all);//ExecMode.Main 下面有解释
    }

    @ToChecker("lambdaTest")// 将函数转换为规则并且注册
    public boolean lambdaTest(String name) {

        return name.length() > 3;
    }
}