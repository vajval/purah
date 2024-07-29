package io.github.vajval.purah.spring.ioc.test_bean.matcher;

import io.github.vajval.purah.core.matcher.WrapListFieldMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.spring.ioc.ann.ToBaseMatcherFactory;

import java.util.Objects;

import static io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcher.NAME;

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
