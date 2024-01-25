package com.purah.checker;

import com.purah.base.Name;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.springboot.ann.MethodsToCheckers;

@MethodsToCheckers
@EnableOnPurahContext
public class CityRateCheckerByAnnMethod {


    @Name("三线城市利率标准检测")
    public boolean test(Double value) {
        if (value > 0.1 || value < 0) {
            return false;
        }
        return true;

    }
}
