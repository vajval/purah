package org.purah.springboot.matcher;

import org.purah.core.name.Name;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.springboot.ann.convert.ToBaseMatcherFactory;

@Name("just_test")
@ToBaseMatcherFactory
public class TestStringMatcher extends BaseStringMatcher {
    public TestStringMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        return false;
    }
}
