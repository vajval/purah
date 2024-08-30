package io.github.vajval.purah.spring.ioc.test_bean.checker;

import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.LambdaChecker;
import io.github.vajval.purah.spring.ioc.ann.PurahMethodsRegBean;
import io.github.vajval.purah.spring.ioc.ann.ToChecker;
import io.github.vajval.purah.spring.ioc.ann.ToCheckerFactory;
import org.springframework.stereotype.Component;


@PurahMethodsRegBean
@Component
public class IocMethodRegTestBean {
    public static final String NOT_NULL_CHECKER_NAME = "not null check for ioc test";
    public static final String RANGE_TEST = "value in [1-3]";
    private static final String RANGE_MATCH = "value in [*-*]";

    @ToCheckerFactory(match = RANGE_MATCH)
    public Checker<Number, Object> range(String name) {
        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return LambdaChecker.of(Number.class).build(i -> range(i, min, max));
    }
    public boolean range(Number value, double min, double max) {
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }

    @ToChecker(NOT_NULL_CHECKER_NAME)
    public boolean IocTestNotNull(Integer o) {
        return o != null;
    }


}
