package org.purah.springboot.matcher;


import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;

@Name("just_test2")
//@EnableOnPurahContext
public class TestStringMatcherNotEnable extends BaseStringMatcher {
    public TestStringMatcherNotEnable(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field) {
        return false;
    }
}
