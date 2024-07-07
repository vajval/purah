package org.purah.core.matcher;


import org.purah.core.base.Name;
import org.purah.core.matcher.inft.FieldMatcher;

@Name("match_all")
public class EqualMatcher extends BaseStringMatcher implements FieldMatcher {


    public EqualMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        return true;
    }
}
