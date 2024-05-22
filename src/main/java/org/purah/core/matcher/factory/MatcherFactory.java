package org.purah.core.matcher.factory;


import org.purah.core.base.IName;
import org.purah.core.matcher.intf.FieldMatcher;

public interface MatcherFactory extends IName {
    FieldMatcher create(String matchStr);

}
