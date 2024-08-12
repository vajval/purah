package io.github.vajval.purah.spring.ioc.test_bean.matcher;

import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.spring.ioc.ann.ToBaseMatcherFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcher.NAME;

@Name(NAME)
@ToBaseMatcherFactory
public class ReverseStringMatcher extends BaseStringMatcher {
    public static final String NAME = "reverse_ioc_test";

    public ReverseStringMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        String reverse = new StringBuilder(matchStr).reverse().toString();
        for (String field : fields) {
            if (Objects.equals(field, reverse)) {
                result.add(field);
                break;
            }
        }
        return result;


    }
}
