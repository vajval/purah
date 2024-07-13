package org.purah.springboot.ioc;

import org.purah.core.name.Name;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.springframework.stereotype.Component;

@Name("IocTestChecker")
@Component
public class IocTestChecker extends AbstractBaseSupportCacheChecker<String, Object> {
    @Override
    public CheckResult doCheck(InputToCheckerArg<String> inputToCheckerArg) {
        if (inputToCheckerArg.argValue().equals("IocTestChecker")) {
            return BaseLogicCheckResult.success();
        }
        return BaseLogicCheckResult.failed(null, "failed");
    }
}
