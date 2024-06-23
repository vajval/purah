package org.purah.springboot.matcher;

import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.springboot.ann.EnableBeanOnPurahContext;

@Name("just_test")
@EnableBeanOnPurahContext
public class TestStringMatcher  extends BaseStringMatcher {
    public TestStringMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        return false;
    }
}
