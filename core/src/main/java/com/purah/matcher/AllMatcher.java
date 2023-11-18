package com.purah.matcher;


import com.purah.base.Name;
import com.purah.matcher.intf.FieldMatcher;

@Name("match_all")
public class AllMatcher extends BaseStringMatcher implements FieldMatcher {


    public AllMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field) {
        return true;
    }
}
