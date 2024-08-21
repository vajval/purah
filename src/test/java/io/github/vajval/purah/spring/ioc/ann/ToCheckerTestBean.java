package io.github.vajval.purah.spring.ioc.ann;

import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.converter.checker.AutoNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@PurahMethodsRegBean
@Component
public class ToCheckerTestBean {
    @Autowired
    Purahs purahs;


    @ToChecker(value = "not_null_test", autoNull = AutoNull.failed,
            failedInfo = "失败 字段 [${path}] 值为[${arg}] ")// 将函数转换为规则并且注册
    public boolean not_null_test(Object name) {
        return true;
    }


    @ToChecker(value = "auto_null_notEnable", autoNull = AutoNull.notEnable)// 将函数转换为规则并且注册
    public boolean auto_null_notEnable(String name) {
        return StringUtils.hasText(name);
    }

    @ToChecker(value = "auto_null_success", autoNull = AutoNull.success)// 将
    public boolean auto_null_success(String name) {
        return false;
    }


    @ToChecker(value = "auto_null_ignore_combo")//
    public Checker<?, ?> auto_null_ignore_combo() {
        return purahs.combo("auto_null_success", "auto_null_ignore").mainMode(ExecMode.Main.all_success);
    }

    @ToChecker(value = "auto_null_ignore", autoNull = AutoNull.ignore)
    public boolean auto_null_ignore(String name) {
        return false;
    }

    @ToChecker(value = "auto_null_failed", autoNull = AutoNull.failed)
    public boolean auto_null_failed(String name) {
        return true;
    }

    @ToChecker(value = "auto_null_ignore_combo_failed")// 将函数转换为规则并且注册
    public Checker<?, ?> auto_null_ignore_combo_failed() {
        return purahs.combo("auto_null_failed", "auto_null_ignore").mainMode(ExecMode.Main.at_least_one);
    }

}
