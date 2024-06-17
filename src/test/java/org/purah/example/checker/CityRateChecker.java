package org.purah.example.checker;

import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.checker.Checker;
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
        return new AbstractBaseSupportCacheChecker<Double, Object>() {
            @Override
            public CheckResult doCheck(InputToCheckerArg<Double> inputToCheckerArg) {
                Double rate = inputToCheckerArg.argValue();
                if (rate < min) {
                    return failed(inputToCheckerArg,"利率值过小为 " + rate);
                }
                if (rate > finalMax) {
                    return failed(inputToCheckerArg,"利率值过大为 " + rate);
                }
                return success(inputToCheckerArg,"利率值正合适");
            }
        };

    }
}
