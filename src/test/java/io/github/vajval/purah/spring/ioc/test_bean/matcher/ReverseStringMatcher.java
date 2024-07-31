package io.github.vajval.purah.spring.ioc.test_bean.matcher;

import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.WrapListFieldMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.spring.ioc.ann.ToBaseMatcherFactory;

import java.util.Objects;

import static io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcher.NAME;

@Name(NAME)
@ToBaseMatcherFactory
public class ReverseStringMatcher extends BaseStringMatcher {
    public static final String NAME = "reverse_ioc_test";
    public ReverseStringMatcher(String matchStr) {

        super(matchStr);
    }
    @Override
    public boolean match(String field, Object belongInstance) {
        String reverse = new StringBuilder(matchStr).reverse().toString();
        return Objects.equals(reverse, field);
    }
}
