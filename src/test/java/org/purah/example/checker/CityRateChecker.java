package org.purah.example.checker;

import org.purah.core.checker.base.BaseCheckerWithCache;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.springboot.ann.EnableOnPurahContext;
import org.springframework.stereotype.Component;

@EnableOnPurahContext
@Component
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
        return new BaseCheckerWithCache<Double, Object>() {
            @Override
            public CheckResult doCheck(CheckInstance<Double> checkInstance) {
                Double rate = checkInstance.instance();
                if (rate < min) {
                    return failed(checkInstance,"利率值过小为 " + rate);
                }
                if (rate > finalMax) {
                    return failed(checkInstance,"利率值过大为 " + rate);
                }
                return success(checkInstance,"利率值正合适");
            }
        };

    }
}
