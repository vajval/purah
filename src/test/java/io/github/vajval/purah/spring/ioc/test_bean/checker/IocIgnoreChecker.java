package io.github.vajval.purah.spring.ioc.test_bean.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.AbstractBaseSupportCacheChecker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.spring.IgnoreBeanOnPurahContext;
import org.springframework.stereotype.Component;

import static io.github.vajval.purah.spring.ioc.test_bean.checker.IocIgnoreChecker.NAME;


@Name(NAME)
@Component
@IgnoreBeanOnPurahContext
public class IocIgnoreChecker extends AbstractBaseSupportCacheChecker<String, Object> {
    public static final String NAME = "IocIgnoreTestChecker";
    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<String> inputToCheckerArg) {
        if (inputToCheckerArg.argValue().equals(NAME)) {
            return LogicCheckResult.success();
        }
        return LogicCheckResult.failed(null, "failed");
    }
}
