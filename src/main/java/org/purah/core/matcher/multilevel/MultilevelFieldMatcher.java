package org.purah.core.matcher.multilevel;


import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.FieldMatcherWithInstance;

public interface MultilevelFieldMatcher extends FieldMatcherWithInstance {

    MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject);


}
