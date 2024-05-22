package org.purah.core.matcher.singleLevel;


import org.apache.commons.io.FilenameUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;

/**
 * 通配符 匹配器
 */
@Name("wild_card")
public class WildCardMatcher extends BaseStringMatcher {

    public WildCardMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field) {
        return FilenameUtils.wildcardMatch(field, matchStr);
    }

}
