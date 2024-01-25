package com.purah.springboot.matcher;

import com.purah.base.Name;
import com.purah.matcher.BaseStringMatcher;
import com.purah.springboot.ann.EnableOnPurahContext;

@Name("just_test")
@EnableOnPurahContext
public class TestStringMatcher  extends BaseStringMatcher {
    public TestStringMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field) {
        return false;
    }
}
