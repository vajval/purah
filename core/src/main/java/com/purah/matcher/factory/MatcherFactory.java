package com.purah.matcher.factory;


import com.purah.base.IName;
import com.purah.matcher.intf.FieldMatcher;

public interface MatcherFactory extends IName {
    FieldMatcher create(String matchStr);

}
