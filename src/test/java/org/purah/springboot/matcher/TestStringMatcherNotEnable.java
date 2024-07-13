package org.purah.springboot.matcher;


import org.purah.core.name.Name;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.springboot.ann.IgnoreBeanOnPurahContext;

@Name("just_test2")
@IgnoreBeanOnPurahContext
public class TestStringMatcherNotEnable extends BaseStringMatcher {
    public TestStringMatcherNotEnable(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        return false;
    }
}
