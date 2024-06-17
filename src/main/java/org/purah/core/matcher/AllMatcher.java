package org.purah.core.matcher;


import org.purah.core.base.Name;

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
