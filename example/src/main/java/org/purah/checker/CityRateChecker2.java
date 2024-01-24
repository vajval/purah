package org.purah.checker;

import com.purah.base.Name;
import com.purah.checker.BaseChecker;
import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import com.purah.checker.context.CheckerResult;
import com.purah.checker.factory.CheckerFactory;
import com.purah.checker.factory.EasyCheckFactory;
import org.purah.springboot.ann.EnableOnPurahContext;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@EnableOnPurahContext
@Component
public class CityRateChecker2 implements EasyCheckFactory<Double> {

    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.endsWith("利率标准检测");
    }

    @Override
    public Predicate<Double> predicate(String needMatchCheckerName) {
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
        return (value) -> value > min && value < max;
    }


}
