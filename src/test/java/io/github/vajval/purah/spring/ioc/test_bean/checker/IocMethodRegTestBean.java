package io.github.vajval.purah.spring.ioc.test_bean.checker;

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
    @ToChecker(NOT_NULL_CHECKER_NAME)
    public boolean IocTestNotNull(Integer o) {
        return o != null;
    }

    @ToCheckerFactory(match = RANGE_MATCH)
    public boolean range(String name, Number value) {
        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }

}
