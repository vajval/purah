package org.purah.core.matcher.factory;


import org.purah.core.base.IName;
import org.purah.core.matcher.inft.FieldMatcher;

public interface MatcherFactory extends IName {

    FieldMatcher create(String matchStr);

}
