package org.purah.springboot.ioc;

import org.purah.core.base.Name;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.springboot.ann.EnableBeanOnPurahContext;
import org.springframework.stereotype.Component;

@EnableBeanOnPurahContext
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
