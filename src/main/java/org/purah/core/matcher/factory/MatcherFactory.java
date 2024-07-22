package org.purah.core.matcher.factory;


import org.purah.core.name.IName;
import org.purah.core.matcher.FieldMatcher;



/**
 * 输入一个字符串,用FieldMatcher的单参string构造器生成
 * This factory generates a fieldMatcher of the specified class using a string parameter.
 */
public interface MatcherFactory extends IName {

    FieldMatcher create(String matchStr);

}
