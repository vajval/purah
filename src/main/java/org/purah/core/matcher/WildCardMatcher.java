package org.purah.core.matcher;

import org.apache.commons.io.FilenameUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.inft.ListableFieldMatcher;

/**
 * a* ->a ab abc
 * a? -> ab ac ad
 */
@Name("wild_card")
public class WildCardMatcher extends ListableFieldMatcher<WildCardMatcher> {


    public WildCardMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    protected WildCardMatcher wrapChildMatcher(String matchStr) {
        return new WildCardMatcher(matchStr);
    }

    @Override
    public boolean matchBySelf(String field, Object belongInstance) {
        return FilenameUtils.wildcardMatch(field, this.matchStr);
    }


}
