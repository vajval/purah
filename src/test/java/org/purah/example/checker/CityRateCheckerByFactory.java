//package org.purah.example.checker;
//
//
//import org.purah.core.checker.factory.EasyCheckFactory;
//
//import java.util.function.Predicate;
//
//
//public class CityRateCheckerByFactory  {
//
//    @Override
//    public boolean match(String needMatchCheckerName) {
//        return needMatchCheckerName.endsWith("市利率标准检测");
//    }
//
//    @Override
//    public Predicate<Double> predicate(String needMatchCheckerName) {
//        double min = 0.1;
//        double max;
//        if (needMatchCheckerName.startsWith("直辖市")) {
//            max = 0.5;
//        } else if (needMatchCheckerName.startsWith("一线城市")) {
//            max = 0.4;
//        } else if (needMatchCheckerName.startsWith("北方城市")) {
//            max = 0.3;
//        } else {
//            max = 0.2;
//        }
//        return (value) -> value > min && value < max;
//    }
//
//
//}
