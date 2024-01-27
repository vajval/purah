package com.purah.matcher.intf;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * 判断字段是否 匹配
 */
public interface FieldMatcher {


    /**
     * 支持的话一定要重写 equal
     * @return
     */
    default boolean supportCache() {
        return false;
    }


    default Set<String> matchFields(Set<String> fields) {
        HashSet<String> result = Sets.newHashSetWithExpectedSize(fields.size());
        for (String field : fields) {
            if (this.match(field)) {
                result.add(field);
            }
        }
        return result;

    }

    boolean match(String field);

}
