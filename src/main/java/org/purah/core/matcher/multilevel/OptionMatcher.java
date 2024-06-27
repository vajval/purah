package org.purah.core.matcher.multilevel;

import com.google.common.collect.Sets;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.util.Set;

public class OptionMatcher extends AbstractMultilevelFieldMatcher {

    String firstLevelStr;
    String childStr;
    FieldMatcher firstLevelFieldMatcher;
    String fullMatchStr;

    public OptionMatcher(String matchStr) {

        super(matchStr);
        fullMatchStr = matchStr;
        int index = matchStr.indexOf(".");
        firstLevelStr = matchStr;
        childStr = "";
        if (index != -1) {
            firstLevelStr = matchStr.substring(0, index);
            childStr = matchStr.substring(index + 1);
        }
        index = firstLevelStr.indexOf("#");
        if (index != -1 && index != 0) {
            childStr = firstLevelStr.substring(index) + "." + childStr;
            firstLevelStr = matchStr.substring(0, index);
        }
        if (!StringUtils.hasText(childStr)) {
            childStr = null;
        }
        firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {

        return firstLevelStr.equals(field);
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {

        return Sets.newHashSet(fullMatchStr);
    }

    @Override
    public MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject) {

        return MultilevelMatchInfo.addToFinal();

    }
}
