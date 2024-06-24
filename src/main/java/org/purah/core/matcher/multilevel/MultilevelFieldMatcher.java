package org.purah.core.matcher.multilevel;


import org.purah.core.matcher.FieldMatcher;

public interface MultilevelFieldMatcher extends FieldMatcher {

    MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject);


}
