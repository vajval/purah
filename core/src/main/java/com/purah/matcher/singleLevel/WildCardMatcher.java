package com.purah.matcher.singleLevel;


import com.purah.base.Name;
import com.purah.matcher.BaseStringMatcher;
import org.apache.commons.io.FilenameUtils;

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
