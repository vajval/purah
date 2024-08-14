package io.github.vajval.purah.spring.ioc.test_bean.checker;

import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.name.Name;
import org.springframework.stereotype.Component;

@Name("中文名字检测")
@Component
public class TestChecker implements Checker<String, Object> {
    @Override
    public CheckResult<Object> check(InputToCheckerArg<String> inputToCheckerArg) {
        String name = inputToCheckerArg.argValue();
        return LogicCheckResult.success();
    }
}
