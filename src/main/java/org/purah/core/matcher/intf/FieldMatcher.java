package org.purah.core.matcher.intf;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * 判断字段是否 匹配
 */
public interface FieldMatcher {


    /**
     * 支持的话一定要重写 equal
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

    default boolean match(String field) {
        return match(field, null);
    }
    default boolean match(String field, Object belongInstance) {
        return match(field);
    }

    default Set<String> matchFields(Set<String> fields, Object belongInstance) {
        HashSet<String> result = Sets.newHashSetWithExpectedSize(fields.size());
        for (String field : fields) {
            if (this.match(field, belongInstance)) {
                result.add(field);
            }
        }
        return result;

    }



}
