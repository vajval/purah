package org.purah.core.matcher.factory;


import org.purah.core.base.IName;
import org.purah.core.matcher.FieldMatcher;



/**
 * This factory generates a fieldMatcher of the specified class using a string parameter.
 */
public interface MatcherFactory extends IName {
    FieldMatcher create(String matchStr);

}
