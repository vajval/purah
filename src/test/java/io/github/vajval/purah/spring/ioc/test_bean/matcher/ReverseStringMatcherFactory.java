package io.github.vajval.purah.spring.ioc.test_bean.matcher;

import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.factory.MatcherFactory;
import io.github.vajval.purah.core.name.Name;
import org.springframework.stereotype.Component;

import static io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcherFactory.NAME;


@Name(NAME)
@Component
public class ReverseStringMatcherFactory implements MatcherFactory
{
    public static final String NAME = "reverse_ioc_test_bean";
    @Override
    public FieldMatcher create(String matchStr) {
        return new ReverseStringMatcher(matchStr);
    }
}
