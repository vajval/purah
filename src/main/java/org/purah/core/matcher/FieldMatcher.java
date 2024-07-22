package org.purah.core.matcher;

import java.util.Set;

/**
 * 从输入的对象中获取想要的字段
 * field matcher for matching fields in input parameters
 */
public interface FieldMatcher {

    //TODO ROOT Match
    String rootField = "$root$";


    /**
     * ChatGPT
     * If supported, you must override equals.
     */
    default boolean supportCache() {
        return false;
    }

    /**
     * 从解析器认为的字段里取出想要的
     * @param fields   Fields as interpreted by the Resolver.
     * @param inputArg inputArg
     * @return match Fields
     */


    Set<String> matchFields(Set<String> fields, Object inputArg);


}
