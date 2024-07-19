package org.purah.springboot.ioc.test_bean.matcher;

import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.name.Name;
import org.springframework.stereotype.Component;

import static org.purah.springboot.ioc.test_bean.matcher.ReverseStringMatcherFactory.NAME;


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
