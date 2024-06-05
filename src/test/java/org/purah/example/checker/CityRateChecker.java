package org.purah.example.checker;

import org.purah.core.checker.base.BaseSupportCacheChecker;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.springboot.ann.EnableBeanOnPurahContext;

@EnableBeanOnPurahContext
public class CityRateChecker implements CheckerFactory {

    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.endsWith("利率标准检测");
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        double min = 0.1;
        double max;
        if (needMatchCheckerName.startsWith("直辖市")) {
            max = 0.5;
        } else if (needMatchCheckerName.startsWith("一线城市")) {
            max = 0.4;
        } else if (needMatchCheckerName.startsWith("北方城市")) {
            max = 0.3;
        } else {
            max = 0.2;
        }
        double finalMax = max;
        return new BaseSupportCacheChecker<Double, Object>() {
            @Override
            public CheckResult doCheck(InputCheckArg<Double> inputCheckArg) {
                Double rate = inputCheckArg.inputArg();
                if (rate < min) {
                    return failed(inputCheckArg,"利率值过小为 " + rate);
                }
                if (rate > finalMax) {
                    return failed(inputCheckArg,"利率值过大为 " + rate);
                }
                return success(inputCheckArg,"利率值正合适");
            }
        };

    }
}
