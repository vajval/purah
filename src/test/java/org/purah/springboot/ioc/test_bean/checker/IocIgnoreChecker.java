package org.purah.springboot.ioc.test_bean.checker;

import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.name.Name;
import org.springframework.stereotype.Component;

import static org.purah.springboot.ioc.test_bean.checker.IocIgnoreChecker.NAME;


@Name(NAME)
@Component
public class IocIgnoreChecker extends AbstractBaseSupportCacheChecker<String, Object> {
    public static final String NAME = "IocIgnoreTestChecker";
    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<String> inputToCheckerArg) {
        if (inputToCheckerArg.argValue().equals(NAME)) {
            return BaseLogicCheckResult.success();
        }
        return BaseLogicCheckResult.failed(null, "failed");
    }
}
