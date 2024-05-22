package org.purah.example.checker;

import org.purah.core.base.Name;
import org.purah.springboot.ann.EnableOnPurahContext;
import org.purah.springboot.ann.PurahEnableMethods;

@PurahEnableMethods
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
