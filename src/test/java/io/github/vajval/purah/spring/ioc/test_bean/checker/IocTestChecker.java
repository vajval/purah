package io.github.vajval.purah.spring.ioc.test_bean.checker;

import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.checker.AbstractBaseSupportCacheChecker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import org.springframework.stereotype.Component;

import static io.github.vajval.purah.spring.ioc.test_bean.checker.IocTestChecker.NAME;

@Name(NAME)
@Component
public class IocTestChecker extends AbstractBaseSupportCacheChecker<String, Object> {

    public static final String NAME = "IocTestChecker";


    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<String> inputToCheckerArg) {
        if (inputToCheckerArg.argValue().equals(NAME)) {
            return LogicCheckResult.success();
        }
        return LogicCheckResult.failed(null, "failed");
    }
}
