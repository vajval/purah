package org.purah.springboot.ioc.test_bean.checker;

import org.purah.core.name.Name;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.result.BaseLogicCheckResult;
import org.purah.core.checker.result.CheckResult;
import org.springframework.stereotype.Component;

import static org.purah.springboot.ioc.test_bean.checker.IocTestChecker.NAME;

@Name(NAME)
@Component
public class IocTestChecker extends AbstractBaseSupportCacheChecker<String, Object> {

    public static final String NAME = "IocTestChecker";


    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<String> inputToCheckerArg) {
        if (inputToCheckerArg.argValue().equals(NAME)) {
            return BaseLogicCheckResult.success();
        }
        return BaseLogicCheckResult.failed(null, "failed");
    }
}
