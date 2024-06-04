package org.purah.springboot.ioc;

import org.purah.core.base.Name;
import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.springboot.ann.EnableBeanOnPurahContext;
import org.springframework.stereotype.Component;

@EnableBeanOnPurahContext
@Name("IocTestChecker")
@Component
public class IocTestChecker extends BaseSupportCacheChecker<String, Object> {
    @Override
    public CheckResult doCheck(CheckInstance<String> checkInstance) {
        if (checkInstance.instance().equals("IocTestChecker")) {
            return BaseLogicCheckResult.success();
        }
        return BaseLogicCheckResult.failed(null, "failed");
    }
}
