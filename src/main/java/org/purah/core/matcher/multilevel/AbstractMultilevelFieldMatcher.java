package org.purah.core.matcher.multilevel;


import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.intf.FieldMatcher;

public abstract class AbstractMultilevelFieldMatcher extends BaseStringMatcher implements MultilevelFieldMatcher {



    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);


    }


    @Override
    public abstract boolean match(String field);



    @Override
    public abstract FieldMatcher childFieldMatcher(String matchedField);
}
