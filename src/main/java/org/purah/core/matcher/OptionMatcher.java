package org.purah.core.matcher;

public class OptionMatcher extends BaseStringMatcher{
    public OptionMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {



        return false;
    }
}
