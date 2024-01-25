package com.purah.springboot.matcher;

import com.purah.base.Name;
import com.purah.matcher.BaseStringMatcher;

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
