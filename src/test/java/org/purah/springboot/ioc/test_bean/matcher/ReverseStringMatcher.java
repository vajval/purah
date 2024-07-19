package org.purah.springboot.ioc.test_bean.matcher;

import org.purah.core.matcher.WrapListFieldMatcher;
import org.purah.core.name.Name;
import org.purah.springboot.ioc.ann.ToBaseMatcherFactory;

import java.util.Objects;

import static org.purah.springboot.ioc.test_bean.matcher.ReverseStringMatcher.NAME;

@Name(NAME)
@ToBaseMatcherFactory
public class ReverseStringMatcher extends WrapListFieldMatcher<ReverseStringMatcher> {

    public static final String NAME = "reverse_ioc_test";


    public ReverseStringMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    protected ReverseStringMatcher wrapChildMatcher(String matchStr) {
        return new ReverseStringMatcher(matchStr);
    }

    @Override
    protected boolean matchBySelf(String field, Object belongInstance) {
        String reverse = new StringBuilder(matchStr).reverse().toString();
        return Objects.equals(reverse, field);
    }


    @Override
    protected boolean matchStrCanCache(String matchSer) {
        return false;
    }
}
