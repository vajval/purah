package org.purah.springboot.ioc.test_bean.checker;

import org.purah.core.name.Name;

import org.purah.springboot.ioc.ann.PurahMethodsRegBean;
import org.purah.springboot.ioc.ann.ToChecker;
import org.purah.springboot.ioc.ann.ToCheckerFactory;



@PurahMethodsRegBean
public class IocMethodRegTestBean {
    public static final String NOT_NULL_CHECKER_NAME = "not null check for ioc test";
    public static final String RANGE_TEST = "value in [1-3]";


    private static final String RANGE_MATCH = "value in [*-*]";
    @ToChecker
    @Name(NOT_NULL_CHECKER_NAME)
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
