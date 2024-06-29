package org.purah.core.matcher.multilevel;

import com.google.common.collect.Sets;
import org.purah.core.base.Name;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.WildCardMatcher;

import java.util.*;
@Name("fixed")
public class FixedMatcher extends AbstractMultilevelFieldMatcher {
    public FixedMatcher(String matchStr) {
        super(matchStr);
    }

    protected MultilevelFieldMatcher wrapChildMatcher(String matchStr) {
        return new FixedMatcher(matchStr);
    }

    @Override
    protected FieldMatcher initFirstLevelFieldMatcher(String str) {
        return new WildCardMatcher(str);
    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        int index = firstLevelStr.indexOf("#");
        if (index == -1) {
            return Collections.emptyMap();
        }
        String substring = firstLevelStr.substring(index + 1);
        int i = Integer.parseInt(substring);
        if (i < objectList.size()) {
            return Collections.singletonMap("#" + i, objectList.get(i));
        }
        return Collections.singletonMap("#" + i, null);
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        if (wrapChildList != null) {
            Set<String> result = new HashSet<>();
            for (MultilevelFieldMatcher optionMatcher : wrapChildList) {
                result.addAll(optionMatcher.matchFields(fields, belongInstance));
            }
            return result;
        }
        if (fields.contains(firstLevelStr)) {
            return Sets.newHashSet(firstLevelStr);
        }
        return Sets.newHashSet(fullMatchStr);
    }
}
