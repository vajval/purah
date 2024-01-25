package com.purah.checker;

import com.purah.checker.context.CheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.springboot.ann.EnableOnPurahContext;
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
        return new BaseChecker<Double, Object>() {
            @Override
            public CheckerResult doCheck(CheckInstance<Double> checkInstance) {
                Double rate = checkInstance.instance();
                if (rate < min) {
                    return failed("利率值过小为 " + rate);
                }
                if (rate > finalMax) {
                    return failed("利率值过大为 " + rate);
                }
                return success("利率值正合适");
            }
        };

    }
}
