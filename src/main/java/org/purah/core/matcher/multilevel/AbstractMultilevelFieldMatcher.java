package org.purah.core.matcher.multilevel;


import org.purah.core.matcher.BaseStringMatcher;

public abstract class AbstractMultilevelFieldMatcher extends BaseStringMatcher implements MultilevelFieldMatcher {


    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);


    }



    @Override
    public abstract MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject);
}
